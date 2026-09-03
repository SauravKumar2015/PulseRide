package com.pulseride.notification.controller;
import java.util.List; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import com.pulseride.notification.service.NotificationService; import lombok.RequiredArgsConstructor;
@RestController @RequestMapping("/notifications") @RequiredArgsConstructor public class NotificationController { private final NotificationService service; @GetMapping("/me") public List<NotificationService.Notification> me(Authentication auth){return service.get(auth.getName());} }
