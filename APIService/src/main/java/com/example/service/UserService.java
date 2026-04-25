package com.example.service;

import com.example.dto.page.PageDTO;
import com.example.dto.user.UserLoginDTO;
import com.example.dto.user.UserUpdateDTO;
import com.example.vo.page.PageVO;
import com.example.vo.user.UserAdminVO;
import com.example.vo.user.UserLoginVO;
import com.example.vo.user.UserVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface UserService {
    // 用户登录
    UserLoginVO login(UserLoginDTO userLoginDTO);
    // 用户注册
    void register(UserLoginDTO userLoginDTO);
    // 用户修改个人信息
    void updateUser(UserUpdateDTO userUpdateDTO);
    // 用户增加关注数
    boolean addOrCancelFollow(@RequestParam Long followUserId);
    // 根据用户id获取用户信息
    UserVO getUserById(Long userId);
    // 管理员获取用户列表
    PageVO<UserAdminVO> getUserList(PageDTO pageDTO);
    // 获取关注列表
    Map<String, Object> getFollowingList(Long userId, Integer page, Integer pageSize);
    // 获取粉丝列表
    Map<String, Object> getFollowerList(Long userId, Integer page, Integer pageSize);
}
