package com.ai_gen.annotation.aop;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ai_gen.annotation.AuthCheck;
import com.ai_gen.entity.User;
import com.ai_gen.enums.ErrorCode;
import com.ai_gen.enums.UserRoleEnum;
import com.ai_gen.exception.BusinessException;
import com.ai_gen.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Aspect
@Component
public class UserRoleInterceptor {

    @Autowired
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {

        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);

        String mustRole = authCheck.mustRole();
        if (StrUtil.isBlank(mustRole)) {
            joinPoint.proceed();
        }

        UserRoleEnum userRoleByValue = UserRoleEnum.getUserRoleByValue(mustRole);
        if (ObjectUtil.isEmpty(userRoleByValue)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"权限值错误");
        }

        UserRoleEnum loginUserRole = UserRoleEnum.getUserRoleByValue(loginUser.getUserRole());
        if (ObjUtil.isEmpty(loginUserRole)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户未开通权限，请联系管理员授权");
        }


        if (Objects.equals(userRoleByValue, loginUserRole)) {
            return joinPoint.proceed();
        }

        else{
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户无对应权限");
        }
    }
}
