package com.example.mapper;

import com.example.entity.Notification;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NotificationMapper {
    // 新增消息通知
    int insert(Notification record);

    //消息已读
    int updateRead(Long userId);

    //查询未读消息通知
    List<Notification> selectByUserId(Long userId);
}
