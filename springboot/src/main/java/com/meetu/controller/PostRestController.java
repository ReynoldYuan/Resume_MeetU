package com.meetu.controller;

import com.meetu.model.Post;
import com.meetu.model.PostDTO;
import com.meetu.service.PostService;
import com.meetu.util.JsonWebTokenUtility;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/posts")
public class PostRestController {

    @Autowired
    private PostService postService;

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    // 驗證 Token 並提取用戶 ID 的私有方法
    private Integer validateTokenAndGetUserId(String authHeader) {
        String token = authHeader.substring(7).trim();
        String userData = jsonWebTokenUtility.validateToken(token);
        if (userData == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        JSONObject userJson = new JSONObject(userData);
        return userJson.getInt("userId");
    }

    // 獲取所有貼文（返回 PostDTO 列表，支援分頁）
    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        List<PostDTO> posts = postService.getAllPostsAsDTO(page, size);
        return ResponseEntity.ok(posts);
    }

    // 獲取當前用戶的所有貼文（返回 PostDTO 列表，支援分頁）
    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(
        @RequestHeader("Authorization") String authHeader,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        try {
            Integer userId = validateTokenAndGetUserId(authHeader);
            List<PostDTO> posts = postService.getPostsByUserIdAsDTO(userId, page, size);
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // 根據ID獲取貼文詳細內容（返回 PostDTO）
    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPostDetail(@PathVariable Integer id) {
        Optional<PostDTO> post = postService.getPostDetailAsDTO(id);
        return post.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    // 創建新貼文
    @PostMapping
    public ResponseEntity<?> createPost(@ModelAttribute Post post,
                                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                        @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = validateTokenAndGetUserId(authHeader);
            post.setPostUserId(userId);

            Post createdPost = postService.createPost(post, imageFile, videoFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the post: " + e.getMessage());
        }
    }

    // 更新貼文
    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(
            @PathVariable Integer id,
            @ModelAttribute Post postDetails,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
            @RequestParam(value = "removeImage", required = false) String removeImage, // 新增刪除圖片標記
            @RequestParam(value = "removeVideo", required = false) String removeVideo, // 新增刪除影片標記
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = validateTokenAndGetUserId(authHeader);
            postDetails.setPostUserId(userId);

            Optional<PostDTO> updatedPost = postService.updatePostAsDTO(id, postDetails, imageFile, videoFile, removeImage, removeVideo);

            return updatedPost.map(ResponseEntity::ok)
                              .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)); // 確保返回正確類型
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    // 軟刪除貼文（將其狀態設為 'D'）
    @PutMapping("/soft-delete/{id}")
    public ResponseEntity<?> softDeletePost(@PathVariable Integer id,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            validateTokenAndGetUserId(authHeader);

            boolean isDeleted = postService.softDeletePost(id);
            if (isDeleted) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete post.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid user or token.");
        }
    }

    // 根據關鍵字搜尋貼文（返回 PostDTO 列表，支援分頁）
    @GetMapping("/search")
    public ResponseEntity<List<PostDTO>> searchPosts(
        @RequestParam("keyword") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        // 檢查關鍵字是否為空或全是空白
        if (keyword == null || keyword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        // 檢查關鍵字長度是否超過5個字
        if (keyword.length() > 5) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<PostDTO> posts = postService.searchPostsByKeywordAsDTO(keyword, page, size);

        // 檢查查詢結果是否為空
        if (posts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        return ResponseEntity.ok(posts);
    }

    // 獲取熱門貼文（返回 PostDTO 列表，支援分頁）
    @GetMapping("/popular")
    public ResponseEntity<List<PostDTO>> getPopularPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        List<PostDTO> posts = postService.getPopularPostsAsDTO(page, size);
        return ResponseEntity.ok(posts);
    }

    // 獲取最新貼文（返回 PostDTO 列表，支援分頁）
    @GetMapping("/latest")
    public ResponseEntity<List<PostDTO>> getLatestPosts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        List<PostDTO> posts = postService.getLatestPostsAsDTO(page, size);
        return ResponseEntity.ok(posts);
    }
}
