package com.ai_gen.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ai_gen.annotation.AuthCheck;
import com.ai_gen.common.DeleteRequest;
import com.ai_gen.entity.App;
import com.ai_gen.entity.User;
import com.ai_gen.enums.ErrorCode;
import com.ai_gen.exception.BusinessException;
import com.ai_gen.model.dto.app.*;
import com.ai_gen.model.vo.AppVO;
import com.ai_gen.response.BaseResponse;
import com.ai_gen.service.AppService;
import com.ai_gen.service.UserService;
import com.ai_gen.utils.ResultUtils;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.ai_gen.constants.UserConstant.ADMIN_ROLE;

/**
 * 应用 控制层。
 *
 * @author jimmy
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;

    /**
     * 用户创建应用
     */
    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody AppAddRequest appAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.addApp(appAddRequest, loginUser));
    }

    /**
     * 用户根据 id 修改自己的应用（只支持修改名称）
     */
    @PostMapping("/my/update")
    public BaseResponse<Boolean> updateMyApp(@RequestBody AppUpdateMyRequest appUpdateMyRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return appService.updateMyApp(appUpdateMyRequest, loginUser);
    }

    /**
     * 用户根据 id 删除自己的应用
     */
    @PostMapping("/my/delete")
    public BaseResponse<Boolean> removeMyApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (ObjUtil.isEmpty(deleteRequest) || ObjUtil.isEmpty(deleteRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.removeMyApp(deleteRequest.getId(), loginUser));
    }

    /**
     * 用户根据 id 查询自己的应用详情
     */
    @GetMapping("/my/get")
    public BaseResponse<AppVO> getMyAppById(@RequestParam Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.getMyAppById(id, loginUser));
    }

    /**
     * 用户分页查询自己的应用列表（支持名称）
     */
    @PostMapping("/my/page")
    public BaseResponse<Page<AppVO>> listMyAppByPage(@RequestBody AppQueryRequest appQueryRequest,
                                                   HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(appService.listMyAppByPage(appQueryRequest, loginUser));
    }

    /**
     * 用户分页查询精选应用列表（支持名称）
     */
    @PostMapping("/good/page")
    public BaseResponse<Page<AppVO>> listGoodAppByPage(@RequestBody AppQueryRequest appQueryRequest) {
        return ResultUtils.success(appService.listGoodAppByPage(appQueryRequest));
    }

    /**
     * 管理员根据 id 删除任意应用
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/admin/delete")
    public BaseResponse<Boolean> adminRemoveApp(@RequestBody DeleteRequest deleteRequest) {
        if (ObjUtil.isEmpty(deleteRequest) || ObjUtil.isEmpty(deleteRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        return ResultUtils.success(appService.adminRemoveApp(deleteRequest.getId()));
    }

    /**
     * 管理员更新任意应用（名称、封面、优先级）
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/admin/update")
    public BaseResponse<Boolean> adminUpdateApp(@RequestBody AppAdminUpdateRequest appAdminUpdateRequest) {
        return ResultUtils.success(appService.adminUpdateApp(appAdminUpdateRequest));
    }

    /**
     * 管理员分页查询应用列表（支持除时间外所有字段）
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @PostMapping("/admin/page")
    public BaseResponse<Page<AppVO>> adminListAppByPage(@RequestBody AppQueryRequest appQueryRequest) {
        return ResultUtils.success(appService.adminListAppByPage(appQueryRequest));
    }

    /**
     * 管理员根据 id 查询应用详情
     */
    @AuthCheck(mustRole = ADMIN_ROLE)
    @GetMapping("/admin/get")
    public BaseResponse<App> adminGetAppById(@RequestParam Long id) {
        return ResultUtils.success(appService.adminGetAppById(id));
    }

    /**
     * 用户请求生成代码接口
     */
    @GetMapping(value = "/chatToGenCode",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatToGenCode(@RequestParam String message,
            @RequestParam Long appId,HttpServletRequest request){
        if (StrUtil.isBlank(message)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"消息不能为空");
        }
        if (ObjUtil.isEmpty(appId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"appId不能为空");
        }
        User loginUser = userService.getLoginUser(request);
        Flux<String> stringFlux = appService.chatToGenCode(message, appId, loginUser);
        return stringFlux.map(chunk -> {
            Map<String,String> data = Map.of("d",chunk);
            String jsonStr = JSONUtil.toJsonStr(data);
            return ServerSentEvent.<String>builder().data(jsonStr).build();
        }).concatWith(Mono.just(ServerSentEvent.<String>builder().event("done").build()));

    }

    /**
     * 部署应用
     */
    @PostMapping(value = "/deploy")
    public BaseResponse<String> deploy(@RequestBody AppDeployRequest appDeployRequest, HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);

        return appService.deployMyApp(appDeployRequest,loginUser);
    }

}
