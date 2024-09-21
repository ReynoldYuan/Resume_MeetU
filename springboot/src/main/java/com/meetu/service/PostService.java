package com.meetu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.meetu.model.Post;
import com.meetu.model.PostDTO;
import com.meetu.model.PostRepository;
import com.meetu.model.UsersProfile;
import com.meetu.model.PostLikeRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    private final String postImageUploadDir = "C:/media/posts/images";
    private final String postVideoUploadDir = "C:/media/posts/videos";

    // 獲取所有未被檢舉成功的貼文並返回 PostDTO 列表，支援分頁
    public List<PostDTO> getAllPostsAsDTO(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> postPage = postRepository.findPostDTOsByPostReportStatus(pageable);
        List<PostDTO> posts = postPage.getContent();
        setLikeCountsForPosts(posts);
        setCommentCountsForPosts(posts);
        return posts;
    }

    // 根據用戶 ID 獲取該用戶的所有未被檢舉成功的貼文並返回 PostDTO 列表，支援分頁
    public List<PostDTO> getPostsByUserIdAsDTO(Integer userId, int page, int size) {
        // 添加按 postUpdatedAt 降序排序的 Pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "postUpdatedAt"));

        // 查詢時使用分頁和排序
        Page<PostDTO> postPage = postRepository.findPostDTOsByUserIdAndPostReportStatus(userId, pageable);
        List<PostDTO> posts = postPage.getContent();
        setLikeCountsForPosts(posts);  // 設置按讚數
        setCommentCountsForPosts(posts);  // 設置評論數
        return posts;
    }

    // 根據關鍵字搜尋貼文並返回 PostDTO 列表，支援分頁
    public List<PostDTO> searchPostsByKeywordAsDTO(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> postPage = postRepository.searchPostDTOsByKeyword(keyword, pageable);
        List<PostDTO> posts = postPage.getContent();
        setLikeCountsForPosts(posts);
        setCommentCountsForPosts(posts);
        return posts;
    }

    // 獲取熱門貼文並返回 PostDTO 列表，支援分頁
    public List<PostDTO> getPopularPostsAsDTO(int page, int size) {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> postPage = postRepository.findPopularPostDTOs(oneMonthAgo, 'N', pageable);
        List<PostDTO> posts = postPage.getContent();
        setLikeCountsForPosts(posts);
        setCommentCountsForPosts(posts);
        return posts;
    }

    // 獲取最新貼文並返回 PostDTO 列表，支援分頁
    public List<PostDTO> getLatestPostsAsDTO(int page, int size) {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        Pageable pageable = PageRequest.of(page, size);
        Page<PostDTO> postPage = postRepository.findLatestPostDTOs(last24Hours, 'N', pageable);
        List<PostDTO> posts = postPage.getContent();
        setLikeCountsForPosts(posts);
        setCommentCountsForPosts(posts);
        return posts;
    }

    // 獲取貼文詳細內容，返回 PostDTO
    public Optional<PostDTO> getPostDetailAsDTO(Integer id) {
        return postRepository.findById(id).map(post -> {
            int likeCount = postLikeRepository.countByPostId(post.getPostId());  // 使用 PostLikeRepository 計算按讚數
            int commentCount = post.getComments().size();
            post.setLikeCount(likeCount);

            UsersProfile userProfile = post.getUser().getUsersProfile();
            return new PostDTO(
                post.getPostId(),
                post.getCaption(),
                post.getImageUrl(),
                post.getVideoUrl(),
                post.getPostCreatedAt(),
                post.getPostUpdatedAt(),
                likeCount,  // 動態計算按讚數量
                commentCount,  // 動態計算留言數量
                post.getPostReportStatus(),
                post.getUser().getUserId(),  // 新增 userId
                userProfile.getUserName(),
                userProfile.getUserPics()
            );
        });
    }

    // 創建貼文
    public Post createPost(Post post, MultipartFile imageFile, MultipartFile videoFile) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = uploadFile(imageFile, postImageUploadDir);
                if (imageUrl != null) {
                    post.setImageUrl(imageUrl);
                }
            }

            if (videoFile != null && !videoFile.isEmpty()) {
                String videoUrl = uploadFile(videoFile, postVideoUploadDir);
                if (videoUrl != null) {
                    post.setVideoUrl(videoUrl);
                }
            }

            post.setPostType(post.getPostType() != null ? post.getPostType() : "POST");
            post.setPostCreatedAt(LocalDateTime.now());
            post.setPostUpdatedAt(LocalDateTime.now());
            post.setPostReportStatus('N');  // 設置預設值為 'N'
            return postRepository.save(post);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create post due to an internal error.", e);
        }
    }

    // 更新貼文
    public Optional<PostDTO> updatePostAsDTO(Integer id, Post postDetails, MultipartFile imageFile, MultipartFile videoFile, String removeImage, String removeVideo) {
        return postRepository.findById(id).map(post -> {
            try {
                // 檢查是否刪除現有的圖片
                if ("true".equals(removeImage)) {
                    deleteFile(post.getImageUrl());
                    post.setImageUrl(null);
                }

                // 檢查是否刪除現有的影片
                if ("true".equals(removeVideo)) {
                    deleteFile(post.getVideoUrl());
                    post.setVideoUrl(null);
                }

                // 更新圖片
                if (imageFile != null && !imageFile.isEmpty()) {
                    String imageUrl = uploadFile(imageFile, postImageUploadDir);
                    if (imageUrl != null) {
                        deleteFile(post.getImageUrl()); // 刪除舊圖片
                        post.setImageUrl(imageUrl);
                    }
                }

                // 更新影片
                if (videoFile != null && !videoFile.isEmpty()) {
                    String videoUrl = uploadFile(videoFile, postVideoUploadDir);
                    if (videoUrl != null) {
                        deleteFile(post.getVideoUrl()); // 刪除舊影片
                        post.setVideoUrl(videoUrl);
                    }
                }

                // 更新其他屬性，但不更新 postReportStatus
                post.setCaption(postDetails.getCaption());
                post.setPostUpdatedAt(LocalDateTime.now());
                post.setPostType(postDetails.getPostType() != null ? postDetails.getPostType() : post.getPostType());

                Post updatedPost = postRepository.save(post);

                UsersProfile userProfile = updatedPost.getUser().getUsersProfile();
                int likeCount = postLikeRepository.countByPostId(updatedPost.getPostId());
                int commentCount = updatedPost.getComments().size();

                PostDTO postDTO = new PostDTO(
                    updatedPost.getPostId(),
                    updatedPost.getCaption(),
                    updatedPost.getImageUrl(),
                    updatedPost.getVideoUrl(),
                    updatedPost.getPostCreatedAt(),
                    updatedPost.getPostUpdatedAt(),
                    likeCount,
                    commentCount,
                    updatedPost.getPostReportStatus(),
                    updatedPost.getUser().getUserId(),  // 新增 userId
                    userProfile.getUserName(),
                    userProfile.getUserPics()
                );

                return postDTO;
            } catch (IOException e) {
                throw new RuntimeException("Failed to update post due to file handling error.", e);
            }
        }).map(Optional::ofNullable).orElse(Optional.empty());
    }


    // 軟刪除貼文
    @Transactional
    public boolean softDeletePost(Integer id) {
        return postRepository.findById(id).map(post -> {
            post.setPostReportStatus('D');  // 將 postReportStatus 設置為 'D'
            postRepository.save(post);
            return true;
        }).orElse(false);
    }

    // 批量設置貼文的 likeCount
    private void setLikeCountsForPosts(List<PostDTO> posts) {
        if (posts.isEmpty()) return;
        List<Integer> postIds = posts.stream().map(PostDTO::getPostId).collect(Collectors.toList());
        
        List<Object[]> likeCounts = postRepository.findLikeCountsByPostIds(postIds);
        Map<Integer, Integer> likeCountMap = likeCounts.stream().collect(Collectors.toMap(
            result -> (Integer) result[0],
            result -> ((Long) result[1]).intValue()
        ));

        posts.forEach(post -> post.setLikeCount(likeCountMap.getOrDefault(post.getPostId(), 0)));
    }

    // 批量設置貼文的 commentCount
    private void setCommentCountsForPosts(List<PostDTO> posts) {
        if (posts.isEmpty()) return;
        List<Integer> postIds = posts.stream().map(PostDTO::getPostId).collect(Collectors.toList());

        List<Object[]> commentCounts = postRepository.findCommentCountsByPostIds(postIds);
        Map<Integer, Integer> commentCountMap = commentCounts.stream().collect(Collectors.toMap(
            result -> (Integer) result[0],
            result -> ((Long) result[1]).intValue()
        ));

        posts.forEach(post -> post.setCommentCount(commentCountMap.getOrDefault(post.getPostId(), 0)));
    }

    // 上傳文件的方法
    private String uploadFile(MultipartFile file, String uploadDir) throws IOException {
        if (!file.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());
            return "/media/posts/" + (uploadDir.equals(postImageUploadDir) ? "images/" : "videos/") + fileName;
        }
        return null;
    }

    // 刪除文件的方法
    private void deleteFile(String fileUrl) {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            Path filePath = null;
            try {
                // 清理並生成文件路徑
                String baseDir = "C:/media/posts/";
                String cleanedFileUrl = fileUrl.replaceFirst("^/media/posts/", ""); // 確保只移除預期的路徑部分
                
                // 構建完整路徑，並檢查路徑是否安全
                filePath = Paths.get(baseDir, cleanedFileUrl).normalize().toAbsolutePath();
                
                if (!filePath.startsWith(Paths.get(baseDir).toAbsolutePath())) {
                    // 如果文件路徑不在預期的目錄下，則不進行刪除操作
                    System.err.println("Unsafe file path detected, aborting delete: " + filePath);
                    return;
                }

                // 刪除文件
                if (Files.deleteIfExists(filePath)) {
                    System.out.println("文件刪除成功: " + filePath);
                } else {
                    System.err.println("文件不存在，無法刪除: " + filePath);
                }
            } catch (IOException e) {
                System.err.println("刪除文件失敗: " + (filePath != null ? filePath.toString() : "null"));
                e.printStackTrace();
            }
        } else {
            System.err.println("文件 URL 為空，無法刪除。");
        }
    }
}
