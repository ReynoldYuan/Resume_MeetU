package com.meetu.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    // 根據用戶 ID 獲取該用戶的所有未被刪除的貼文，返回 DTO，支援分頁
    @Query("SELECT new com.meetu.model.PostDTO(p.postId, p.caption, p.imageUrl, p.videoUrl, "
            + "p.postCreatedAt, p.postUpdatedAt, 0L, 0L, "
            + "p.postReportStatus, p.user.userId, p.user.usersProfile.userName, p.user.usersProfile.userPics) "
            + "FROM Post p "
            + "WHERE p.postUserId = :userId AND p.postReportStatus = 'N'")
    Page<PostDTO> findPostDTOsByUserIdAndPostReportStatus(@Param("userId") Integer userId, Pageable pageable);

    // 查詢所有未被刪除的貼文，返回 DTO，支援分頁
    @Query("SELECT new com.meetu.model.PostDTO(p.postId, p.caption, p.imageUrl, p.videoUrl, "
            + "p.postCreatedAt, p.postUpdatedAt, 0L AS likeCount, 0L AS commentCount, p.postReportStatus, "
            + "p.user.userId, p.user.usersProfile.userName, p.user.usersProfile.userPics) "
            + "FROM Post p "
            + "WHERE p.postReportStatus = 'N'")
    Page<PostDTO> findPostDTOsByPostReportStatus(Pageable pageable);

    // 根據關鍵字搜尋貼文，只在 caption 中搜尋，並過濾掉已刪除的貼文，返回 DTO，支援分頁
    @Query("SELECT new com.meetu.model.PostDTO(p.postId, p.caption, p.imageUrl, p.videoUrl, "
            + "p.postCreatedAt, p.postUpdatedAt, "  
            + "(SELECT COUNT(pl) FROM PostLike pl WHERE pl.targetId = p.postId AND pl.targetType = 'POST') AS likeCount, "  
            + "(SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.postId = p.postId AND pc.commentReportStatus = 'N') AS commentCount, "  // 修改這裡
            + "p.postReportStatus, "
            + "p.user.userId, p.user.usersProfile.userName, p.user.usersProfile.userPics) "
            + "FROM Post p "
            + "WHERE p.caption IS NOT NULL AND p.caption LIKE %:keyword% AND p.postReportStatus = 'N'")
    Page<PostDTO> searchPostDTOsByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 獲取熱門貼文：按讚數量和留言數量，限制在一個月內，並過濾掉已刪除的貼文，返回 DTO，支援分頁
    @Query("SELECT new com.meetu.model.PostDTO(p.postId, p.caption, p.imageUrl, p.videoUrl, "
            + "p.postCreatedAt, p.postUpdatedAt, " 
            + "(SELECT COUNT(pl) FROM PostLike pl WHERE pl.targetId = p.postId AND pl.targetType = 'POST') AS likeCount, "
            + "(SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.postId = p.postId AND pc.commentReportStatus = 'N') AS commentCount, "  // 修改這裡
            + "p.postReportStatus, "
            + "p.user.userId, p.user.usersProfile.userName, p.user.usersProfile.userPics) "
            + "FROM Post p "
            + "WHERE p.postCreatedAt >= :oneMonthAgo AND p.postReportStatus = :postReportStatus AND p.postReportStatus != 'D' "
            + "ORDER BY ("
            + "   (SELECT COUNT(pl) * 2 FROM PostLike pl WHERE pl.targetId = p.postId AND pl.targetType = 'POST') + "
            + "   (SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.postId = p.postId AND pc.commentReportStatus = 'N')"  // 修改這裡
            + ") DESC, p.postCreatedAt DESC")
    Page<PostDTO> findPopularPostDTOs(@Param("oneMonthAgo") LocalDateTime oneMonthAgo,
                                      @Param("postReportStatus") char postReportStatus,
                                      Pageable pageable);

    // 獲取最新貼文：24 小時內的貼文，並過濾掉已刪除的貼文，返回 DTO，支援分頁
    @Query("SELECT new com.meetu.model.PostDTO(p.postId, p.caption, p.imageUrl, p.videoUrl, "
            + "p.postCreatedAt, p.postUpdatedAt, " 
            + "(SELECT COUNT(pl) FROM PostLike pl WHERE pl.targetId = p.postId AND pl.targetType = 'POST') AS likeCount, "
            + "(SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.postId = p.postId AND pc.commentReportStatus = 'N') AS commentCount, "  // 修改這裡
            + "p.postReportStatus, "
            + "p.user.userId, p.user.usersProfile.userName, p.user.usersProfile.userPics) "
            + "FROM Post p WHERE p.postCreatedAt >= :last24Hours AND p.postReportStatus = :postReportStatus")
    Page<PostDTO> findLatestPostDTOs(@Param("last24Hours") LocalDateTime last24Hours,
                                     @Param("postReportStatus") char postReportStatus,
                                     Pageable pageable);

    // 獲取所有熱門貼文，根據按讚數和留言數量排序，支援分頁
    @Query("SELECT new com.meetu.model.PostDTO(p.postId, p.caption, p.imageUrl, p.videoUrl, "
            + "p.postCreatedAt, p.postUpdatedAt, " 
            + "(SELECT COUNT(pl) FROM PostLike pl WHERE pl.targetId = p.postId AND pl.targetType = 'POST') AS likeCount, "
            + "(SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.postId = p.postId AND pc.commentReportStatus = 'N') AS commentCount, "  // 修改這裡
            + "p.postReportStatus, "
            + "p.user.userId, p.user.usersProfile.userName, p.user.usersProfile.userPics) "
            + "FROM Post p "
            + "WHERE p.postReportStatus = 'N' "
            + "ORDER BY ("
            + "   (SELECT COUNT(pl) * 2 FROM PostLike pl WHERE pl.targetId = p.postId AND pl.targetType = 'POST') + "
            + "   (SELECT COUNT(pc) FROM PostComment pc WHERE pc.post.postId = p.postId AND pc.commentReportStatus = 'N')"  // 修改這裡
            + ") DESC")
    Page<PostDTO> findPopularPosts(Pageable pageable);

    // 根據 postIds 批量查詢 Post 和 UsersProfile，生成 PostDTO 所需的數據，並過濾掉已刪除的貼文，支援分頁
    @Query("SELECT new com.meetu.model.PostDTO(p.postId, p.caption, p.imageUrl, p.videoUrl, "
            + "p.postCreatedAt, p.postUpdatedAt, p.postReportStatus, " 
            + "p.user.userId, up.userName, up.userPics) "
            + "FROM Post p LEFT JOIN p.user.usersProfile up "
            + "WHERE p.postId IN :postIds AND p.postReportStatus != 'D' "
            + "GROUP BY p.postId, p.caption, p.imageUrl, p.videoUrl, p.postCreatedAt, p.postUpdatedAt, p.postReportStatus, p.user.userId, up.userName, up.userPics")
    Page<PostDTO> findPostDTOsByPostIds(@Param("postIds") List<Integer> postIds, Pageable pageable);
    
    // 根據 postIds 獲取每篇貼文的 like 數量
    @Query("SELECT p.postId, COUNT(l) as likeCount FROM Post p "
            + "LEFT JOIN PostLike l ON l.targetId = p.postId AND l.targetType = 'POST' "
            + "WHERE p.postId IN :postIds GROUP BY p.postId")
    List<Object[]> findLikeCountsByPostIds(@Param("postIds") List<Integer> postIds);

    // 根據 postIds 獲取每篇貼文的留言數量，過濾掉已刪除的留言
    @Query("SELECT p.postId, COUNT(c) as commentCount FROM Post p "
            + "LEFT JOIN PostComment c ON c.post.postId = p.postId AND c.commentReportStatus = 'N' "  // 修改這裡
            + "WHERE p.postId IN :postIds GROUP BY p.postId")
    List<Object[]> findCommentCountsByPostIds(@Param("postIds") List<Integer> postIds);
}
