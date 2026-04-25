package com.example.mapper;

import com.example.entity.User;
import com.example.vo.user.UserLoginVO;
import com.example.vo.user.UserPostListVO;
import com.example.vo.user.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {

    // 根据用户名查询用户
    User selectByUsername(String username);

    // 根据用户id查询用户
    UserVO selectUserById(Long userId);

    //用户注册
    int register(User user);

    // 根据用户id查询用户登录时返回的个人信息
    UserLoginVO selectById(Long adminId);

    // 根据用户id查询不同用户头像和昵称
    UserPostListVO selectUserPostById(Long userId);

    //修改用户信息
    int updateById(User user);

    //获赞数+1
    int increaseLikeCount(Long userId);

    //获赞数-1
    int decreaseLikeCount(Long userId);

    //粉丝数+1
    int increaseFansCount(Long userId);

    //粉丝数-1
    int decreaseFansCount(Long userId);

    //关注数+1
    int increaseFollowCount(Long userId);

    //关注数-1
    int decreaseFollowCount(Long userId);


    //管理员获取用户列表信息
    List<User> getUserList(@Param("offset") Long offset,
                                  @Param("size") Integer size,
                                  @Param("keyword") String keyword);

    //管理员获取用户列表总条数
    Long getUserCount(@Param("keyword") String keyword);

    //管理员修改用户信息
    int adminUpdateUserById(User user);
}
