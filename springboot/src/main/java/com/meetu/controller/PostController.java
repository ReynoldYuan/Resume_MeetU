//package com.meetu.controller;
//
//import com.meetu.model.Post;
//import com.meetu.model.Users;
//import com.meetu.service.PostService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import jakarta.servlet.http.HttpSession;
//
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/posts")
//public class PostController {
//
//    @Autowired
//    private PostService postService;
//
//    // 獲取所有貼文列表
//    @GetMapping("/postsList")
//    public String getAllPosts(Model model) {
//        model.addAttribute("posts", postService.getAllPosts());
//        return "post/postsList";
//    }
//
//    // 查看單個貼文的詳細信息
//    @GetMapping("/postDetail/{id}")
//    public String getPostById(@PathVariable Integer id, Model model) {
//        Post post = postService.getPostById(id).orElse(null);
//        model.addAttribute("post", post);
//        return "post/postDetail";
//    }
//
//    // 顯示創建貼文的表單
//    @GetMapping("/createPost")
//    public String showCreatePostForm(Model model) {
//        model.addAttribute("post", new Post());
//        return "post/createPost";
//    }
//
//    // 創建貼文
//    @PostMapping("/create")
//    public String createPost(@ModelAttribute Post post, 
//                             @RequestParam("imageFile") MultipartFile imageFile, 
//                             @RequestParam("videoFile") MultipartFile videoFile, 
//                             HttpSession session, Model model) {
//        Users user = (Users) session.getAttribute("loginUser");
//
//        if (user != null) {
//            post.setPostUserId(user.getUserId());
//        } else {
//            model.addAttribute("error", "User must be logged in to create a post.");
//            return "redirect:/posts/createPost";
//        }
//
//        Post createdPost = postService.createPost(post, imageFile, videoFile);
//
//        if (createdPost != null) {
//            return "redirect:/posts/postDetail/" + createdPost.getPostId();
//        } else {
//            model.addAttribute("error", "Failed to create post.");
//            return "post/createPost";
//        }
//    }
//
//    // 顯示編輯貼文的表單
//    @GetMapping("/edit/{id}")
//    public String showEditPostForm(@PathVariable Integer id, Model model) {
//        Post post = postService.getPostById(id).orElse(null);
//        model.addAttribute("post", post);
//        return "post/editPost";
//    }
//
//    // 更新貼文
//    @PostMapping("/update/{id}")
//    public String updatePost(@PathVariable Integer id, 
//                             @ModelAttribute Post post, 
//                             @RequestParam("imageFile") MultipartFile imageFile,
//                             @RequestParam("videoFile") MultipartFile videoFile, 
//                             HttpSession session, Model model) {
//        Users user = (Users) session.getAttribute("loginUser");
//
//        if (user != null) {
//            post.setPostUserId(user.getUserId());
//        } else {
//            model.addAttribute("error", "User must be logged in to edit a post.");
//            return "redirect:/posts/edit/" + id;
//        }
//
//        Optional<Post> optionalUpdatedPost = postService.updatePost(id, post, imageFile, videoFile);
//        Post updatedPost = optionalUpdatedPost.orElse(null);
//
//        if (updatedPost != null) {
//            return "redirect:/posts/postDetail/" + updatedPost.getPostId();
//        } else {
//            model.addAttribute("error", "Failed to update post.");
//            return "post/editPost";
//        }
//    }
//
//    // 刪除貼文
//    @PostMapping("/delete/{id}")
//    public String deletePost(@PathVariable Integer id, HttpSession session, Model model) {
//        Users user = (Users) session.getAttribute("loginUser");
//
//        if (user == null) {
//            model.addAttribute("error", "User must be logged in to delete a post.");
//            return "redirect:/posts/postsList";
//        }
//
//        boolean isDeleted = postService.deletePost(id);
//
//        if (isDeleted) {
//            return "redirect:/posts/postsList";
//        } else {
//            model.addAttribute("error", "Failed to delete post.");
//            return "post/postsList";
//        }
//    }
//}
