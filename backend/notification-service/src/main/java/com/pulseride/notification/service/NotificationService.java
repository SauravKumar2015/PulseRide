package com.pulseride.notification.service;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
@Service public class NotificationService { public record Notification(String id,String userId,String type,String message,Instant createdAt){} private final Map<String,List<Notification>> byUser=new ConcurrentHashMap<>(); public void publish(String userId,String type,String message){byUser.computeIfAbsent(userId,k->new CopyOnWriteArrayList<>()).add(new Notification(UUID.randomUUID().toString(),userId,type,message,Instant.now()));} public List<Notification> get(String userId){return List.copyOf(byUser.getOrDefault(userId,List.of()));} }
