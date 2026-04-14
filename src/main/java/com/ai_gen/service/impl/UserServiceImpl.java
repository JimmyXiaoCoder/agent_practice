package com.ai_gen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.ai_gen.entity.User;
import com.ai_gen.exception.BusinessException;
import com.ai_gen.mapper.UserMapper;
import com.ai_gen.model.vo.LoginUserVO;
import com.ai_gen.service.UserService;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.ai_gen.constants.UserConstant.USER_LOGIN_STATE;
import static com.ai_gen.enums.ErrorCode.*;

/**
 * 用户 服务层实现。
 *
 * @author jimmy
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {

        if (StrUtil.hasBlank(userAccount,userPassword,checkPassword)) {
            throw new BusinessException(PARAMS_ERROR,"请输入完整注册信息");
        }

        // 1. 校验注册表单
        if (userAccount.length() < 4) {
            throw new BusinessException(PARAMS_ERROR,"用户账号长度不小于4位");
        }

        if (userPassword.length() < 8) {
            throw new BusinessException(PARAMS_ERROR,"用户密码长度不小于8位");
        }

        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(PARAMS_ERROR,"两次输入密码不一致");
        }

        // 2. 检验是否重复
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount",userAccount);
        long l = this.mapper.selectCountByQuery(queryWrapper);
        if(l > 0) {
            throw new BusinessException(PARAMS_ERROR,"账号已被注册");
        }

        // 3. 密码加密处理
        String secretPassword = getEncryptPassword(userPassword);

        // 4. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(secretPassword);
        user.setUserName("默认名称");

        if (this.save(user)) {
            return user.getId();
        }
        else {
            throw new BusinessException(SYSTEM_ERROR,"系统异常，用户注册失败");
        }

    }

    @Override
    public String getEncryptPassword(String userPassword) {
        // 盐值，混淆密码
        final String SALT = "yupi";
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
    }

    @Override
    public LoginUserVO getLoginUserVO(User user) {

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user,loginUserVO);
        return loginUserVO;
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        if (StrUtil.hasBlank(userAccount,userPassword)) {
            throw new BusinessException(PARAMS_ERROR,"账号/密码不能为空");
        }

        QueryWrapper query = new QueryWrapper();
        query.eq("userAccount",userAccount);
        User user = this.mapper.selectOneByQuery(query);

        String encryptPassword = getEncryptPassword(userPassword);
        if(encryptPassword.equals(user.getUserPassword())) {
            request.getSession().setAttribute(USER_LOGIN_STATE,user);
            return getLoginUserVO(user);
        }
        else{
            throw new BusinessException(PARAMS_ERROR,"账号/密码错误");
        }
    }

    @Override
    public void userLogout(HttpServletRequest request) {

        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (ObjUtil.isEmpty(user)) {
            throw new BusinessException(NOT_LOGIN_ERROR);
        }

        request.getSession().removeAttribute(USER_LOGIN_STATE);

    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (ObjUtil.isEmpty(user)) {
            throw new BusinessException(NOT_LOGIN_ERROR);
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",user.getId());
        User nowUser = this.mapper.selectOneByQuery(queryWrapper);

        return nowUser;
    }

}
