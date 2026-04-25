package com.example.mapper;


import com.example.entity.Follow;
import com.example.vo.user.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FollowMapper {
    //关注
    int insert(Follow follow);

    //取消关注
    int deleteById(@Param("id") Long id);

    //查询关注
    Follow selectByUserIdAndFollowId(@Param("userId") Long userId, @Param("followUserId") Long followUserId);

    //获取关注列表
    List<UserVO> selectFollowingList(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    //获取粉丝列表
    List<UserVO> selectFollowerList(@Param("userId") Long userId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    //获取关注总数
    int countFollowing(@Param("userId") Long userId);

    //获取粉丝总数
    int countFollowers(@Param("userId") Long userId);
}
