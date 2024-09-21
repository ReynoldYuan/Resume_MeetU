package com.meetu.service;

import com.meetu.model.Post;
import com.meetu.model.PostComment;
import com.meetu.model.PostCommentDTO;
import com.meetu.model.PostCommentRepository;
import com.meetu.model.PostLikeRepository;
import com.meetu.model.PostRepository;
import com.meetu.model.Users;
import com.meetu.model.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostCommentService {

    private static final Logger logger = LoggerFactory.getLogger(PostCommentService.class);

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private PostLikeService postLikeService;

    private PostComment getCommentOrThrow(Integer commentId) {
        return postCommentRepository.findById(commentId).orElseThrow(() -> {
            logger.error("Comment not found with ID: {}", commentId);
            return new IllegalArgumentException("Comment not found with ID: " + commentId);
        });
    }

    private Users getUserOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    private Post getPostOrThrow(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with ID: " + postId));
    }

    @Transactional
    public List<PostCommentDTO> deleteComment(Integer commentId, Integer userId) {
        // 獲取要刪除的評論
        PostComment comment = getCommentOrThrow(commentId);

        // 檢查用戶是否有權限刪除該評論
        if (!comment.getUser().getUserId().equals(userId)) {
            logger.error("User {} does not有權限刪除ID為 {} 的評論", userId, commentId);
            throw new IllegalArgumentException("您無權刪除此評論。");
        }

        // 執行軟刪除
        comment.setCommentReportStatus('D');
        postCommentRepository.save(comment); // 保存已刪除狀態的評論
        logger.info("評論已標記為刪除，ID: {}", commentId);

        // 返回更新後的評論列表
        return getAllCommentsForPost(comment.getPost().getPostId());
    }

 // 獲取貼文的所有評論（狀態為 'N' 的評論）
    public List<PostCommentDTO> getAllCommentsForPost(Integer postId) {
        // 使用查詢方法並設置無限大的 PageRequest 來確保獲取所有符合條件的評論
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE); // 獲取所有評論
        Page<PostCommentDTO> commentsPage = postCommentRepository.findByPostPostIdAndCommentReportStatus(postId, pageable);
        
        return commentsPage.getContent(); // 返回評論列表
    }
    
    @Transactional
    public List<PostCommentDTO> createComment(PostCommentDTO commentDTO, Integer userId) {
        try {
            if (commentDTO == null || commentDTO.getPostId() == null) {
                logger.error("Invalid comment or post data: {}", commentDTO);
                throw new IllegalArgumentException("Invalid comment or post data.");
            }

            PostComment comment = new PostComment();
            comment.setCommentContent(commentDTO.getCommentContent());
            comment.setCommentType(commentDTO.getCommentType() == null ? "TEXT" : commentDTO.getCommentType());
            comment.setCommentCreatedAt(LocalDateTime.now());
            comment.setCommentUpdatedAt(LocalDateTime.now());
            comment.setCommentReportStatus('N');

            Users currentUser = getUserOrThrow(userId);
            comment.setUser(currentUser);

            Post post = getPostOrThrow(commentDTO.getPostId());
            comment.setPost(post);

            PostComment savedComment = postCommentRepository.save(comment);
            logger.info("Comment created with ID: {}", savedComment.getCommentId());

            // 使用 findByPostPostIdAndCommentReportStatus 來獲取正常狀態的留言列表
            List<PostCommentDTO> updatedComments = postCommentRepository.findByPostPostIdAndCommentReportStatus(commentDTO.getPostId(), PageRequest.of(0, Integer.MAX_VALUE)).getContent();

            return updatedComments;
        } catch (Exception e) {
            logger.error("Error creating comment: ", e);
            throw new RuntimeException("An error occurred while creating the comment.");
        }
    }

    @Transactional(readOnly = true)
    public Page<PostCommentDTO> getCommentsByPostId(Integer postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostCommentDTO> commentsPage = postCommentRepository.findByPostPostIdAndCommentReportStatus(postId, pageable);
        
        commentsPage.forEach(comment -> comment.setLikeCount(postLikeService.getLikeCountByCommentId(comment.getCommentId())));
        
        return commentsPage;
    }

    @Transactional(readOnly = true)
    public PostCommentDTO getCommentById(Integer commentId) {
        PostComment comment = getCommentOrThrow(commentId);
        PostCommentDTO commentDTO = new PostCommentDTO(comment);
        
        commentDTO.setLikeCount(postLikeService.getLikeCountByCommentId(comment.getCommentId()));
        
        return commentDTO;
    }
    
    @Transactional
    public PostCommentDTO editComment(Integer commentId, String newContent, Integer userId) {
        try {
            PostComment comment = getCommentOrThrow(commentId);

            if (!comment.getUser().getUserId().equals(userId)) {
                logger.error("User {} does not have permission to edit comment with ID: {}", userId, commentId);
                throw new IllegalArgumentException("You do not have permission to edit this comment.");
            }

            comment.setCommentContent(newContent);
            comment.setCommentUpdatedAt(LocalDateTime.now());

            PostComment updatedComment = postCommentRepository.save(comment);
            logger.info("Comment updated with ID: {}", updatedComment.getCommentId());

            PostCommentDTO updatedCommentDTO = new PostCommentDTO(updatedComment);
            updatedCommentDTO.setLikeCount(postLikeService.getLikeCountByCommentId(updatedComment.getCommentId()));

            return updatedCommentDTO;
        } catch (Exception e) {
            logger.error("Error editing comment: ", e);
            throw new RuntimeException("An error occurred while editing the comment.");
        }
    }
}
