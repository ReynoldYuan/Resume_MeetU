package com.meetu.controller;

import com.meetu.model.Users;
import com.meetu.service.PostLikeService;
import com.meetu.util.JsonWebTokenUtility;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@CrossOrigin
@RestController
@RequestMapping("/api/likes")
public class PostLikeController {

    @Autowired
    private PostLikeService postLikeService;

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    // 驗證 Token 並提取用戶 ID 的私有方法
    private Integer validateTokenAndGetUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid authorization header");
        }
        String token = authHeader.substring(7).trim();
        String userData = jsonWebTokenUtility.validateToken(token);
        if (userData == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        JSONObject userJson = new JSONObject(userData);
        return userJson.getInt("userId");
    }

    // 切換貼文按讚狀態
 
    @PostMapping("/post/{postId}/toggle")
    public ResponseEntity<Map<String, Object>> togglePostLike(@PathVariable Integer postId,
                                                              @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = validateTokenAndGetUserId(authHeader);
            boolean isLiked = postLikeService.togglePostLike(postId, userId);
            int likeCount = postLikeService.getLikeCountByPostId(postId);
            return ResponseEntity.ok(Map.of("liked", isLiked, "likeCount", likeCount, "message", isLiked ? "Liked" : "Unliked"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred"));
        }
    }

    // 切換留言按讚狀態

    @PostMapping("/comment/{commentId}/toggle")
    public ResponseEntity<Map<String, Object>> toggleCommentLike(@PathVariable Integer commentId,
                                                                 @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = validateTokenAndGetUserId(authHeader);
            boolean isLiked = postLikeService.toggleCommentLike(commentId, userId);
            int likeCount = postLikeService.getLikeCountByCommentId(commentId);
            return ResponseEntity.ok(Map.of("liked", isLiked, "likeCount", likeCount, "message", isLiked ? "Liked" : "Unliked"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred"));
        }
    }

 // 獲取貼文的按讚用戶列表
 
    @GetMapping("/post/{postId}/users")
    public ResponseEntity<List<Users>> getUsersWhoLikedPost(@PathVariable Integer postId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Users> users = postLikeService.getUsersWhoLikedPost(postId, page, size);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // 獲取留言的按讚用戶列表
  
    @GetMapping("/comment/{commentId}/users")
    public ResponseEntity<List<Users>> getUsersWhoLikedComment(@PathVariable Integer commentId,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        try {
            List<Users> users = postLikeService.getUsersWhoLikedComment(commentId, page, size);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // 獲取貼文的按讚數
  
    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Integer> getPostLikeCount(@PathVariable Integer postId) {
        try {
            int likeCount = postLikeService.getLikeCountByPostId(postId);
            return ResponseEntity.ok(likeCount);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // 獲取留言的按讚數

    @GetMapping("/comment/{commentId}/count")
    public ResponseEntity<Integer> getCommentLikeCount(@PathVariable Integer commentId) {
        try {
            int likeCount = postLikeService.getLikeCountByCommentId(commentId);
            return ResponseEntity.ok(likeCount);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    // 檢查使用者是否對某貼文按讚
 
    @GetMapping("/post/{postId}/is-liked")
    public ResponseEntity<?> isPostLikedByUser(@PathVariable Integer postId,
                                               @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = validateTokenAndGetUserId(authHeader);
            boolean isLiked = postLikeService.isPostLikedByUser(postId, userId);
            return ResponseEntity.ok(Map.of("liked", isLiked));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred"));
        }
    }


    @GetMapping("/comment/{commentId}/is-liked")
    public ResponseEntity<?> isCommentLikedByUser(@PathVariable Integer commentId,
                                                  @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = validateTokenAndGetUserId(authHeader);
            boolean isLiked = postLikeService.isCommentLikedByUser(commentId, userId);
            return ResponseEntity.ok(Map.of("liked", isLiked));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "An unexpected error occurred"));
        }
    }

    // 處理預檢請求
    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public ResponseEntity<?> handleOptionsRequest() {
        return ResponseEntity
            .ok()
            .header("Access-Control-Allow-Origin", "http://localhost:5173")
            .header("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")
            .header("Access-Control-Allow-Headers", "Authorization, Content-Type")
            .build();
    }
}