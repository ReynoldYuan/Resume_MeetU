package com.meetu.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.ActivitiesCollect;
import com.meetu.service.ActivitiesCollectService;
import com.meetu.service.ActivitiesService;
import com.meetu.util.JsonWebTokenUtility;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@CrossOrigin
public class ActivitiesCollectController {

    @Autowired
    private ActivitiesCollectService actCollectService;

    @Autowired
    public JsonWebTokenUtility jsonWebTokenUtility;
    
    @Autowired
    public ActivitiesService aService;

    @PostMapping("/collect")
    public ResponseEntity<?> addToCollect(@RequestBody Map<String, Integer> requestBody, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);

            if (userData == null) {
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
            }

            JSONObject userJson = new JSONObject(userData);
            Integer userId = userJson.getInt("userId");
            Integer activitiesId = requestBody.get("activitiesId");

            if (activitiesId == null) {
                return ResponseEntity.badRequest().body("activitiesId is required");
            }

            actCollectService.addToCollect(userId, activitiesId);

            return ResponseEntity.ok("Activity collected successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/collect")
    public ResponseEntity<?> findUserCollects(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);

            if (userData == null) {
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
            }

            JSONObject userJson = new JSONObject(userData);
            Integer userId = userJson.getInt("userId");

            List<ActivitiesCollect> collects = actCollectService.findActCollectByUserId(userId);

            List<Map<String, Object>> collectDetails = new ArrayList<>();
            for (ActivitiesCollect collect : collects) {
                Map<String, Object> details = new HashMap<>();
                details.put("actCollectId", collect.getActCollectId().toString());
                details.put("activitiesId", collect.getActivities().getActivitiesId());
                details.put("activitiesTitle", collect.getActivities().getActivitiesTitle());
                details.put("activitiesStartDate", collect.getActivities().getActivitiesStartDate());
                details.put("activitiesEndDate", collect.getActivities().getActivitiesEndDate());
                details.put("activitiesReportStatus", collect.getActivities().getActivitiesReportStatus());
                details.put("activitiesLocation", collect.getActivities().getActivitiesLocation());
                collectDetails.add(details);
            }

            return ResponseEntity.ok(collectDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
    }


    @DeleteMapping("/collect/{activitiesId}")
    public ResponseEntity<?> deleteCollect(@PathVariable Integer activitiesId, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);

            if (userData == null) {
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
            }

            JSONObject userJson = new JSONObject(userData);
            Integer userId = userJson.getInt("userId");

            actCollectService.deleteCollect(userId, activitiesId);

            return ResponseEntity.ok("Activity removed from collection");
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
    }
    
    @GetMapping("/collect/check/{activitiesId}")
    public ResponseEntity<?> checkIfActivityCollected(@PathVariable Integer activitiesId, @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);

            if (userData == null) {
                return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body("Invalid token");
            }

            JSONObject userJson = new JSONObject(userData);
            Integer userId = userJson.getInt("userId");

            boolean isCollected = actCollectService.isActivityCollectedByUser(userId, activitiesId);

            return ResponseEntity.ok(isCollected);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
    }
    
    @GetMapping("/collect/activity/{activitiesId}")
    public ResponseEntity<?> getActivityCollectors(@PathVariable Integer activitiesId) {
        try {
            Map<String, Object> response = actCollectService.getActivityCollectorsInfo(activitiesId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                                 .body("An error occurred: " + e.getMessage());
        }
    }
}