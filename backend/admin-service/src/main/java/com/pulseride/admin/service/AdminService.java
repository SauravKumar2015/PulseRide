package com.pulseride.admin.service;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
@Service public class AdminService { public record Audit(String actorId,String action,String resourceType,String resourceId,Instant timestamp,String result){} private final List<Audit> audit=new CopyOnWriteArrayList<>(); private final Map<String,String> statuses=new ConcurrentHashMap<>(); public List<Map<String,Object>> empty(){return List.of();} public Map<String,Object> metrics(){return Map.of("totalUsers",0,"activeDrivers",0,"ridesToday",0,"completedRides",0,"cancelledRides",0,"successfulPayments",0,"failedPayments",0,"activeSurgeZones",0);} public void status(String actor,String type,String id,String value){statuses.put(type+":"+id,value);audit.add(new Audit(actor,"STATUS_CHANGED",type,id,Instant.now(),"SUCCESS"));} public List<Audit> audit(){return List.copyOf(audit);} }
