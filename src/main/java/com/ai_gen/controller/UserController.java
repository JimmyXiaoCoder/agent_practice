package com.ai_gen.controller;

import cn.hutool.core.bean.BeanUtil;
import com.ai_gen.annotation.AuthCheck;
import com.ai_gen.entity.User;
import com.ai_gen.model.dto.UserLoginRequest;
import com.ai_gen.model.dto.UserRegisterRequest;
import com.ai_gen.model.vo.UserVO;
import com.ai_gen.response.BaseResponse;
import com.ai_gen.service.UserService;
import com.ai_gen.utils.ResultUtils;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户 控制层。
 *
 * @author jimmy
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest){
        long l = userService.userRegister(userRegisterRequest.getUserAccount(), userRegisterRequest.getUserPassword(), userRegisterRequest.getCheckPassword());
        return ResultUtils.success(l);
    }

    @PostMapping("/login")
    public BaseResponse<UserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        UserVO userVO = userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword(), request);
        return ResultUtils.success(userVO);
    }

    @GetMapping("/getCurUser")
//    @AuthCheck(mustRole = "admin")
    public BaseResponse<UserVO> getCurUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    @GetMapping("/logout")
    public BaseResponse<UserVO> logout(HttpServletRequest request) {
        userService.userLogout(request);
        return ResultUtils.success();
    }

    /**
     * 根据主键删除用户。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/remove/{id}")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> remove(@PathVariable Long id) {
        return ResultUtils.success(userService.removeById(id));
    }

    /**
     * 根据主键更新用户。
     *
     * @param user 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/admin/update")
    @AuthCheck(mustRole = "admin")
    public boolean update(@RequestBody User user) {
        return userService.updateById(user);
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     */
    @GetMapping("/admin/list")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<UserVO>> list() {
        List<User> list = userService.list();
        List<UserVO> userVOList = list.stream().map(user -> {
            UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 根据主键获取用户。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("getInfo/{id}")
    public User getInfo(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * 分页查询用户。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("/admin/page")
    @AuthCheck(mustRole = "admin")
    public Page<User> page(Page<User> page) {
        return userService.page(page);
    }

}
