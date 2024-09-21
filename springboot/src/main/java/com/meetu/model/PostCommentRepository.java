package com.meetu.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {

    // 查詢所有特定貼文的正常狀態留言，支持分頁，返回 PostCommentDTO
    @Query("SELECT new com.meetu.model.PostCommentDTO(c) " +
           "FROM PostComment c " +
           "WHERE c.post.postId = :postId AND c.commentReportStatus = 'N' " +  // 過濾狀態為 'N'
           "ORDER BY c.commentCreatedAt DESC")
    Page<PostCommentDTO> findByPostPostIdAndCommentReportStatus(@Param("postId") Integer postId, Pageable pageable);

    // 查詢留言並支持分頁，返回 PostCommentDTO（保留以防未來擴展使用）
    @Query("SELECT new com.meetu.model.PostCommentDTO(c) " +
           "FROM PostComment c " +
           "WHERE c.post.postId = :postId AND c.commentReportStatus = 'N' " +  // 過濾狀態為 'N'
           "ORDER BY c.commentCreatedAt DESC")
    Page<PostCommentDTO> findCommentsByPostId(@Param("postId") Integer postId, Pageable pageable);
}
