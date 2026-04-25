package com.example.service;

import com.example.entity.Notification;

import java.util.List;

public interface NotificationService {
    int getUnreadCount(Long userId);

    void clearUnread(Long userId);

}
