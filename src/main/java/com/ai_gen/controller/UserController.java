package com.ai_gen.controller;

import com.ai_gen.entity.User;
import com.ai_gen.model.dto.UserLoginRequest;
import com.ai_gen.model.dto.UserRegisterRequest;
import com.ai_gen.model.vo.LoginUserVO;
import com.ai_gen.response.BaseResponse;
import com.ai_gen.service.UserService;
import com.ai_gen.utils.ResultUtils;
import com.mybatisflex.core.paginate.Page;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        LoginUserVO loginUserVO = userService.userLogin(userLoginRequest.getUserAccount(), userLoginRequest.getUserPassword(), request);
        return ResultUtils.success(loginUserVO);
    }

    @GetMapping("/getCurUser")
//    @AuthCheck(mustRole = "admin")
    public BaseResponse<LoginUserVO> getCurUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    @GetMapping("/logout")
    public BaseResponse<LoginUserVO> logout(HttpServletRequest request) {
        userService.userLogout(request);
        return ResultUtils.success();
    }

    /**
     * 根据主键删除用户。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Long id) {
        return userService.removeById(id);
    }

    /**
     * 根据主键更新用户。
     *
     * @param user 用户
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody User user) {
        return userService.updateById(user);
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<User> list() {
        return userService.list();
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
    @GetMapping("page")
    public Page<User> page(Page<User> page) {
        return userService.page(page);
    }

}
