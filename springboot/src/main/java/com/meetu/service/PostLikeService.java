package com.meetu.service;

import com.meetu.model.Post;
import com.meetu.model.PostRepository;
import com.meetu.model.PostComment;
import com.meetu.model.PostCommentRepository;
import com.meetu.model.PostLike;
import com.meetu.model.PostLikeRepository;
import com.meetu.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostLikeService {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostRepository postRepository;  // 注入 PostRepository

    @Autowired
    private PostCommentRepository postCommentRepository; // 注入 PostCommentRepository

    // 切換對貼文的按讚狀態
    @Transactional
    public boolean togglePostLike(Integer postId, Integer userId) {
        PostLike postLike = postLikeRepository.findByPostIdAndLikeUserId(postId, userId);
        if (postLike != null) {
            postLikeRepository.delete(postLike);

            // 更新 Post 的 likeCount
            Post post = postRepository.findById(postId)
                           .orElseThrow(() -> new IllegalArgumentException("Post not found"));
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);

            return false;
        } else {
            postLike = new PostLike();
            postLike.setTargetType("POST");
            postLike.setTargetId(postId);
            postLike.setLikeUserId(userId);
            postLikeRepository.save(postLike);

            // 更新 Post 的 likeCount
            Post post = postRepository.findById(postId)
                           .orElseThrow(() -> new IllegalArgumentException("Post not found"));
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);

            return true;
        }
    }

    // 切換對評論的按讚狀態
    @Transactional
    public boolean toggleCommentLike(Integer commentId, Integer userId) {
        PostLike postLike = postLikeRepository.findByCommentIdAndLikeUserId(commentId, userId);
        if (postLike != null) {
            postLikeRepository.delete(postLike);

            // 更新 PostComment 的 likeCount
            PostComment comment = postCommentRepository.findById(commentId)
                                      .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
            comment.decrementLikeCount();
            postCommentRepository.save(comment);

            return false;
        } else {
            postLike = new PostLike();
            postLike.setTargetType("COMMENT");
            postLike.setTargetId(commentId);
            postLike.setLikeUserId(userId);
            postLikeRepository.save(postLike);

            // 更新 PostComment 的 likeCount
            PostComment comment = postCommentRepository.findById(commentId)
                                      .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
            comment.incrementLikeCount();
            postCommentRepository.save(comment);

            return true;
        }
    }

    public int getLikeCountByPostId(Integer postId) {
        return postLikeRepository.countByPostId(postId);
    }

    public int getLikeCountByCommentId(Integer commentId) {
        return postLikeRepository.countByCommentId(commentId);
    }

    public boolean isPostLikedByUser(Integer postId, Integer userId) {
        return postLikeRepository.existsByPostIdAndUserId(postId, userId);
    }

    public boolean isCommentLikedByUser(Integer commentId, Integer userId) {
        return postLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    // 使用分頁獲取對某貼文按讚的用戶列表
    public List<Users> getUsersWhoLikedPost(Integer postId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Users> usersPage = postLikeRepository.findUsersByPostId(postId, pageRequest);
        return usersPage.getContent();
    }

    // 使用分頁獲取對某留言按讚的用戶列表
    public List<Users> getUsersWhoLikedComment(Integer commentId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Users> usersPage = postLikeRepository.findUsersByCommentId(commentId, pageRequest);
        return usersPage.getContent();
    }
}
