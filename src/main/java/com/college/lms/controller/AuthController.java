package com.college.lms.controller;

import com.college.lms.entity.Notification;
import com.college.lms.entity.User;
import com.college.lms.service.NotificationService;
import com.college.lms.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;
    private final NotificationService notificationService;

    public AuthController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String role = userDetails.getAuthorities().iterator().next().getAuthority();
            if (role.equals("ROLE_ADMIN")) return "redirect:/admin/dashboard";
            if (role.equals("ROLE_TEACHER")) return "redirect:/teacher/dashboard";
            if (role.equals("ROLE_STUDENT")) return "redirect:/student/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please verify your credentials.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }
        return "auth/login";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) return "redirect:/login";
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        model.addAttribute("currentUser", user);

        if (user.getRole().name().equals("ROLE_STUDENT")) {
            userService.getStudentProfileByUser(user).ifPresent(p -> model.addAttribute("studentProfile", p));
        } else if (user.getRole().name().equals("ROLE_TEACHER")) {
            userService.getTeacherProfileByUser(user).ifPresent(p -> model.addAttribute("teacherProfile", p));
        }

        return "auth/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam String fullName,
                                @RequestParam(required = false) String phone,
                                RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        userService.updateUserProfile(user, fullName, phone);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
        return "redirect:/profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Passwords do not match.");
            return "redirect:/profile";
        }
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        userService.updatePassword(user, newPassword);
        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
        return "redirect:/profile";
    }

    @GetMapping("/notifications")
    public String notifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Notification> notifications = notificationService.getNotificationsForUser(user);
        model.addAttribute("notifications", notifications);
        model.addAttribute("currentUser", user);
        return "auth/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id, @RequestHeader(value = "Referer", required = false) String referer) {
        notificationService.markAsRead(id);
        return "redirect:" + (referer != null ? referer : "/notifications");
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        notificationService.markAllAsRead(user);
        return "redirect:/notifications";
    }
}
