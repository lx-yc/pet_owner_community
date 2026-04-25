package com.example.controller;

import com.example.dto.page.PageDTO;
import com.example.dto.user.UserFollowIdDTO;
import com.example.dto.user.UserLoginDTO;
import com.example.dto.user.UserUpdateDTO;
import com.example.service.UserService;
import com.example.utils.Result;
import com.example.vo.page.CursorPageVO;
import com.example.vo.page.PageVO;
import com.example.vo.user.UserAdminVO;
import com.example.vo.user.UserLoginVO;
import com.example.vo.user.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "用户模块", description = "用户登录、注册、信息管理等接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/detail/{id}")
    public Result<UserVO> getUserById(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        return Result.success(user, "获取用户信息成功");
    }


    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        UserLoginVO user = userService.login(userLoginDTO);
        return Result.success(user, "登录成功");
    }

    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserLoginDTO userLoginDTO) {
        userService.register(userLoginDTO);
        return Result.success("注册成功");
    }

    @GetMapping("/list")
    public Result<PageVO<UserAdminVO>> getUserList( PageDTO pageDTO) {
        return Result.success(userService.getUserList(pageDTO), "获取用户列表成功");
    }

    @PostMapping("/update")
    public Result<Void> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        userService.updateUser(userUpdateDTO);
        return Result.success("更新用户信息成功");
    }

    @PostMapping("/follow")
    public Result<Boolean> follow(@RequestBody UserFollowIdDTO dot) {
        boolean isFollow = userService.addOrCancelFollow(dot.getFollowUserId());
        return Result.success(isFollow ? "关注成功" : "取消关注成功");
    }

    @Operation(summary = "获取关注列表", description = "分页获取指定用户的关注列表")
    @GetMapping("/{userId}/following")
    public Result<Map<String, Object>> getFollowingList(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(userService.getFollowingList(userId, page, pageSize), "获取关注列表成功");
    }

    @Operation(summary = "获取粉丝列表", description = "分页获取指定用户的粉丝列表")
    @GetMapping("/{userId}/followers")
    public Result<Map<String, Object>> getFollowerList(@PathVariable Long userId,
                                                        @RequestParam(defaultValue = "1") Integer page,
                                                        @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(userService.getFollowerList(userId, page, pageSize), "获取粉丝列表成功");
    }
}
