package com.meetu.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

	
	// 刪除與特定貼文或留言相關的點讚記錄
    @Modifying
    @Transactional  // 確保方法在事務中運行
    @Query("DELETE FROM PostLike pl WHERE pl.targetType = :targetType AND pl.targetId = :targetId")
    void deleteByTargetTypeAndTargetId(@Param("targetType") String targetType, @Param("targetId") Integer targetId);
	
	
    // 查詢某貼文的愛心數量
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.targetType = 'POST' AND pl.targetId = :postId")
    int countByPostId(@Param("postId") Integer postId);

    // 查詢某留言的愛心數量
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.targetType = 'COMMENT' AND pl.targetId = :commentId")
    int countByCommentId(@Param("commentId") Integer commentId);

    // 檢查用戶是否已經點讚了特定的貼文
    @Query("SELECT COUNT(pl) > 0 FROM PostLike pl WHERE pl.targetType = 'POST' AND pl.targetId = :postId AND pl.user.userId = :userId")
    boolean existsByPostIdAndUserId(@Param("postId") Integer postId, 
                                    @Param("userId") Integer userId);

    // 檢查用戶是否已經點讚了特定的留言
    @Query("SELECT COUNT(pl) > 0 FROM PostLike pl WHERE pl.targetType = 'COMMENT' AND pl.targetId = :commentId AND pl.user.userId = :userId")
    boolean existsByCommentIdAndUserId(@Param("commentId") Integer commentId, 
                                       @Param("userId") Integer userId);

    // 獲取貼文的按讚用戶列表（支持分頁）
    @Query("SELECT pl.user FROM PostLike pl WHERE pl.targetType = 'POST' AND pl.targetId = :postId")
    Page<Users> findUsersByPostId(@Param("postId") Integer postId, Pageable pageable);

    // 獲取留言的按讚用戶列表（支持分頁）
    @Query("SELECT pl.user FROM PostLike pl WHERE pl.targetType = 'COMMENT' AND pl.targetId = :commentId")
    Page<Users> findUsersByCommentId(@Param("commentId") Integer commentId, Pageable pageable);
    
 // 根據 postId 和 likeUserId 查找貼文點讚
    @Query("SELECT pl FROM PostLike pl WHERE pl.targetType = 'POST' AND pl.targetId = :postId AND pl.user.userId = :likeUserId")
    PostLike findByPostIdAndLikeUserId(@Param("postId") Integer postId, 
                                       @Param("likeUserId") Integer likeUserId);

    // 根據 commentId 和 likeUserId 查找留言點讚
    @Query("SELECT pl FROM PostLike pl WHERE pl.targetType = 'COMMENT' AND pl.targetId = :commentId AND pl.user.userId = :likeUserId")
    PostLike findByCommentIdAndLikeUserId(@Param("commentId") Integer commentId, 
                                          @Param("likeUserId") Integer likeUserId);
}
