package com.meetu.controller;

import com.meetu.model.PostCommentDTO;
import com.meetu.service.PostCommentService;
import com.meetu.util.JsonWebTokenUtility;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/comments")
public class PostCommentController {

    private static final Logger logger = LoggerFactory.getLogger(PostCommentController.class);

    @Autowired
    private PostCommentService postCommentService;

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    private Integer validateTokenAndGetUserId(String authHeader) {
        String token = authHeader.substring(7).trim();
        String userData = jsonWebTokenUtility.validateToken(token);
        if (userData == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        JSONObject userJson = new JSONObject(userData);
        return userJson.getInt("userId");
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody PostCommentDTO commentDTO,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            if (commentDTO == null || commentDTO.getPostId() == null) {
                logger.error("Invalid comment or post data received.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid comment or post data.");
            }

            Integer userId = validateTokenAndGetUserId(authHeader);
            List<PostCommentDTO> updatedComments = postCommentService.createComment(commentDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedComments);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating comment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the comment.");
        }
    }	

    @PutMapping("/{commentId}/soft-delete")
    public ResponseEntity<?> softDeleteComment(
            @PathVariable Integer commentId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 通過 token 驗證並獲取 userId
            Integer userId = validateTokenAndGetUserId(authHeader);

            // 調用服務層的方法來軟刪除留言
            List<PostCommentDTO> updatedComments = postCommentService.deleteComment(commentId, userId);

            // 返回更新後的留言列表
            return ResponseEntity.ok().body(updatedComments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 返回 403 禁止訪問狀態
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the comment."); // 返回 500 錯誤狀態
        }
    }



    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentDetail(@PathVariable Integer commentId) {
        try {
            PostCommentDTO commentDTO = postCommentService.getCommentById(commentId);
            if (commentDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found.");
            }
            return ResponseEntity.ok(commentDTO);
        } catch (Exception e) {
            logger.error("Error retrieving comment detail: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the comment detail.");
        }
    }

    @GetMapping("/post/{postId}/sorted")
    public ResponseEntity<Page<PostCommentDTO>> getSortedComments(@PathVariable Integer postId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        try {
            Page<PostCommentDTO> comments = postCommentService.getCommentsByPostId(postId, page, size);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            logger.error("Error retrieving sorted comments: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<PostCommentDTO> editComment(
            @PathVariable Integer commentId,
            @RequestBody PostCommentDTO commentDTO,  // 接收 DTO
            @RequestHeader("Authorization") String authHeader) {
        try {
            // 通過 token 驗證並獲取 userId
            Integer userId = validateTokenAndGetUserId(authHeader);

            // 確保 commentContent 存在且不為空
            if (commentDTO == null || commentDTO.getCommentContent() == null || commentDTO.getCommentContent().isEmpty()) {
                throw new IllegalArgumentException("Comment content cannot be empty.");
            }

            // 調用服務層的方法來編輯留言
            PostCommentDTO updatedComment = postCommentService.editComment(commentId, commentDTO.getCommentContent(), userId);
            return ResponseEntity.ok(updatedComment);  // 返回 DTO 對象
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 返回 400 錯誤狀態
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // 返回 500 錯誤狀態
        }
    }

    // 處理 OPTIONS 預檢請求
    @RequestMapping(value = "/{commentId}/soft-delete", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptionsRequestForSoftDelete() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{commentId}", method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> handleOptionsRequestForEdit() {
        return ResponseEntity.ok().build();
    }

    // 全局異常處理
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Handled IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Handled RuntimeException: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred.");
    }
}
