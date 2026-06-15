package com.ai_gen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.ai_gen.constants.AppConstant;
import com.ai_gen.constants.UserConstant;
import com.ai_gen.core.AiCodeGeneratorFacade;
import com.ai_gen.entity.App;
import com.ai_gen.entity.User;
import com.ai_gen.enums.CodeGenTypeEnum;
import com.ai_gen.enums.ErrorCode;
import com.ai_gen.exception.BusinessException;
import com.ai_gen.mapper.AppMapper;
import com.ai_gen.model.dto.app.*;
import com.ai_gen.model.vo.AppVO;
import com.ai_gen.model.vo.UserVO;
import com.ai_gen.response.BaseResponse;
import com.ai_gen.service.AppService;
import com.ai_gen.service.ChatHistoryService;
import com.ai_gen.service.UserService;
import com.ai_gen.utils.RandomUtil;
import com.ai_gen.utils.ResultUtils;
import com.ai_gen.utils.ThrowUtils;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author jimmy
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService,AppConstant {

    private static final int USER_MAX_PAGE_SIZE = 20;

    @Resource
    private UserService userService;

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Override
    public Long addApp(AppAddRequest appAddRequest, User loginUser) {
        if (ObjUtil.hasEmpty(appAddRequest, loginUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        if (StrUtil.isBlank(appAddRequest.getInitPrompt())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "initPrompt 不能为空");
        }
        App app = new App();
        String initPrompt = appAddRequest.getInitPrompt();
        // 暂时设置为提示词前 12 位
        app.setAppName(StrUtil.blankToDefault(appAddRequest.getAppName(), initPrompt.substring(Math.min(12,initPrompt.length()))));
        app.setInitPrompt(appAddRequest.getInitPrompt());
        app.setCover(appAddRequest.getCover());
        // 暂时设置为多文件
        app.setCodeGenType(CodeGenTypeEnum.MULTI_FILE.getValue());
        app.setUserId(loginUser.getId());
        app.setPriority(0);
        boolean result = this.save(app);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建应用失败");
        }
        return app.getId();
    }

    @Override
    public BaseResponse<Boolean> updateMyApp(AppUpdateMyRequest appUpdateMyRequest, User loginUser) {
        if (ObjUtil.hasEmpty(appUpdateMyRequest, loginUser, appUpdateMyRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        if (StrUtil.isBlank(appUpdateMyRequest.getAppName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称不能为空");
        }
        App oldApp = getById(appUpdateMyRequest.getId());
        if (ObjUtil.isEmpty(oldApp)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        if (!oldApp.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限修改该应用");
        }
        App updateApp = new App();
        updateApp.setId(appUpdateMyRequest.getId());
        updateApp.setAppName(appUpdateMyRequest.getAppName());
        // 设置编辑时间
        updateApp.setEditTime(LocalDateTime.now());
        boolean result = this.updateById(updateApp);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    @Override
    public boolean removeMyApp(Long id, User loginUser) {
        if (ObjUtil.hasEmpty(id, loginUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        App oldApp = getById(id);
        if (ObjUtil.isEmpty(oldApp)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        if (!oldApp.getUserId().equals(loginUser.getId()) || loginUser.getUserRole().equals(UserConstant.ADMIN_ROLE)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除该应用");
        }
        // 级联删除对话历史
        chatHistoryService.removeByAppId(id);
        return this.removeById(id);
    }

    @Override
    public AppVO getMyAppById(Long id, User loginUser) {
        if (ObjUtil.hasEmpty(id, loginUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        App app = getById(id);
        if (ObjUtil.isEmpty(app)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限查看该应用");
        }
        // 查询应用关联用户信息，不用LoginUser，因为可能是管理员
        User userInfo = userService.getById(app.getUserId());
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app,appVO);

        UserVO userVO = BeanUtil.copyProperties(userInfo, UserVO.class);
        appVO.setUserVO(userVO);

        return appVO;
    }

    @Override
    public Page<AppVO> listMyAppByPage(AppQueryRequest appQueryRequest, User loginUser) {

        if (ObjUtil.hasEmpty(appQueryRequest, loginUser)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        int pageNum = appQueryRequest.getPageNum();
        int pageSize = appQueryRequest.getPageSize();
        if (pageSize > USER_MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "每页最多 20 条");
        }
        // 只能查询当前登录用户
        appQueryRequest.setUserId(loginUser.getId());
        QueryWrapper queryWrapper = getQueryWrapper(appQueryRequest);
        if (StrUtil.isNotBlank(appQueryRequest.getAppName())) {
            queryWrapper.like("appName", appQueryRequest.getAppName());
        }
        Page<App> page = this.page(Page.of(pageNum, pageSize), queryWrapper);
        Page<AppVO> appVOPage = new Page<>(pageNum,pageSize,page.getTotalRow());
        appVOPage.setRecords(getAppVOList(page.getRecords()));

        return appVOPage;
    }

    @Override
    public Page<AppVO> listGoodAppByPage(AppQueryRequest appQueryRequest) {
        if (ObjUtil.isEmpty(appQueryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        int pageSize = appQueryRequest.getPageSize();
        int pageNum = appQueryRequest.getPageNum();
        if (pageSize > USER_MAX_PAGE_SIZE) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "每页最多 20 条");
        }
        QueryWrapper queryWrapper = getQueryWrapper(appQueryRequest);
        queryWrapper.eq("priority", AppConstant.GOOD_APP_PRIORITY);
        Page<App> page = this.page(Page.of(pageNum, pageSize), queryWrapper);
        Page<AppVO> appVOPage = new Page<>(pageNum,pageSize,page.getTotalRow());
        appVOPage.setRecords(getAppVOList(page.getRecords()));

        return appVOPage;
    }

    @Override
    public boolean adminRemoveApp(Long id) {
        if (ObjUtil.isEmpty(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        App app = getById(id);
        if (ObjUtil.isEmpty(app)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        // 级联删除对话历史
        chatHistoryService.removeByAppId(id);
        return this.removeById(id);
    }

    @Override
    public boolean adminUpdateApp(AppAdminUpdateRequest appAdminUpdateRequest) {
        if (ObjUtil.hasEmpty(appAdminUpdateRequest, appAdminUpdateRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        App oldApp = getById(appAdminUpdateRequest.getId());
        if (ObjUtil.isEmpty(oldApp)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        App updateApp = new App();
        updateApp.setId(appAdminUpdateRequest.getId());
        updateApp.setAppName(appAdminUpdateRequest.getAppName());
        updateApp.setCover(appAdminUpdateRequest.getCover());
        updateApp.setPriority(appAdminUpdateRequest.getPriority());
        return this.updateById(updateApp);
    }

    @Override
    public App adminGetAppById(Long id) {
        if (ObjUtil.isEmpty(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        App app = getById(id);
        if (ObjUtil.isEmpty(app)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        }
        return app;
    }

    @Override
    public Page<AppVO> adminListAppByPage(AppQueryRequest appQueryRequest) {
        if (ObjUtil.isEmpty(appQueryRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        QueryWrapper queryWrapper = getQueryWrapper(appQueryRequest);

        int pageNum = appQueryRequest.getPageNum();
        int pageSize = appQueryRequest.getPageSize();

        Page<App> page = this.page(Page.of(pageNum, pageSize), queryWrapper);
        Page<AppVO> appVOPage = new Page<>(pageNum,pageSize,page.getTotalRow());
        appVOPage.setRecords(getAppVOList(page.getRecords()));

        return appVOPage;
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }

    @Override
    public List<AppVO> getAppVOList(List<App> appList) {

        Set<Long> userIdSet = appList.stream().map(App::getUserId).collect(Collectors.toSet());

        List<User> users = userService.listByIds(userIdSet);
        Map<Long, UserVO> userVOMap = users.stream().collect(Collectors.toMap(User::getId, userService::getLoginUserVO));

        List<AppVO> appVOList = appList.stream().map(app -> {
            AppVO appVO = new AppVO();
            BeanUtil.copyProperties(app, appVO);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUserVO(userVO);
            return appVO;
        }).collect(Collectors.toList());

        return appVOList;
    }

    @Override
    public Flux<String> chatToGenCode(String message, Long appId, User loginUser) {

        // 传参校验
        if (StrUtil.isBlank(message)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"消息不能为空");
        }
        if (ObjUtil.isEmpty(appId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"appId不能为空");
        }

        // 查询app信息
        App app = this.getById(appId);
        if (ObjUtil.isEmpty(app)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"应用不存在");
        }

        // 只能用户本人修改应用信息
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR,"无修改应用权限");
        }

        // 查询代码生成类型
        CodeGenTypeEnum typeByValue = CodeGenTypeEnum.getTypeByValue(app.getCodeGenType());
        if (ObjUtil.isEmpty(typeByValue)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不支持的代码生成类型");
        }

        Flux<String> stringFlux = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, typeByValue, appId, loginUser.getId());

        return stringFlux;
    }

    @Override
    public BaseResponse<String> deployMyApp(AppDeployRequest appDeployRequest, User loginUser) {
        App app = this.getById(appDeployRequest.getId());

        // 用户只能部署自己的应用
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"只能部署自己的应用");
        }

        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey  = RandomUtil.generateRandomKey();
            // 查重
            do {
                AppQueryRequest appQueryRequest = new AppQueryRequest();
                appQueryRequest.setDeployKey(deployKey);
                QueryWrapper queryWrapper = getQueryWrapper(appQueryRequest);
                List<App> list = this.list(queryWrapper);
                if (CollectionUtil.isEmpty(list)) {
                    break;
                }
                deployKey = RandomUtil.generateRandomKey();
            }
            while (true);
            appDeployRequest.setDeployKey(deployKey);
        }
        App deployedApp = BeanUtil.copyProperties(appDeployRequest, App.class);
        deployedApp.setDeployedTime(LocalDateTime.now());

        this.updateById(deployedApp);

        // todo 部署到 nginx 服务器目录
        CodeGenTypeEnum typeByValue = CodeGenTypeEnum.getTypeByValue(app.getCodeGenType());
        String codeGenPath = CODE_OUTPUT_ROOT_DIR + File.separator + typeByValue.getValue() + "_" + app.getId();
        String deployPath = CODE_DEPLOY_ROOT_DIR + File.separator + typeByValue.getValue() + "_" + app.getDeployKey();
        File sourceFile = new File(codeGenPath);
        File deployFile = new File(deployPath);
        FileUtil.copyContent(sourceFile,deployFile,true);

        // 返回访问地址
        return ResultUtils.success(CODE_DEPLOY_HOST + "/" + typeByValue.getValue() + "_" + deployKey);
    }


}
