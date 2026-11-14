package com.college.lms.config;

import com.college.lms.entity.Notification;
import com.college.lms.entity.User;
import com.college.lms.service.NotificationService;
import com.college.lms.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;
    private final NotificationService notificationService;

    public GlobalModelAttributes(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @ModelAttribute("currentUser")
    public User getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return userService.findByEmail(userDetails.getUsername()).orElse(null);
        }
        return null;
    }

    @ModelAttribute("unreadNotificationCount")
    public long getUnreadNotificationCount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return userService.findByEmail(userDetails.getUsername())
                    .map(notificationService::getUnreadCount)
                    .orElse(0L);
        }
        return 0L;
    }

    @ModelAttribute("topNotifications")
    public List<Notification> getTopNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return userService.findByEmail(userDetails.getUsername())
                    .map(notificationService::getTopNotificationsForUser)
                    .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }
}
