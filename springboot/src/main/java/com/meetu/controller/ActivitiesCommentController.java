//package com.meetu.controller;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.meetu.model.Activities;
//import com.meetu.model.ActivitiesComment;
//import com.meetu.model.Users;
//import com.meetu.service.ActivitiesCommentService;
//import com.meetu.service.ActivitiesService;
//import com.meetu.service.UserService;
//
//import jakarta.servlet.http.HttpSession;
//
////@Controller
//@RestController
//@CrossOrigin
//public class ActivitiesCommentController {
//
//	@Autowired
//	private ActivitiesCommentService commentService;
//
//	@Autowired
//	private ActivitiesService actService;
//
//	@Autowired
//	private UserService userService;

//C----------------------------------------------------------
	
//	@GetMapping("/activities/addComment")
//	public String addActivitiesComment() {
//		return "activities/addCommentPage";
//	}
//
//	@PostMapping("/activities/addCommentPost")
//	public String addActivitiesCommentPost(@RequestParam String messageContent, @RequestParam Integer activityId,
//			Model model, HttpSession httpSession) {
//
//		// 有沒有人登入
//		Users users = (Users) httpSession.getAttribute("loginUser");
//
//		// 有人登入
//		if (users != null) {
//
//			ActivitiesComment actComment = new ActivitiesComment();
//			actComment.setActivitiesReportStatus("N");
//			actComment.setMessageContent(messageContent);
//			actComment.setUsers(users);
//
//			Activities activities = actService.findActById(activityId);
//
//			actComment.setActivities(activities);
//
//			commentService.saveComment(actComment);
//		} else {
//			// 沒人登入
//
//			model.addAttribute("okMsg", "請先登入");
//			return "users/loginPage";
//
//		}
//		return "redirect:/activities/userslist";
//	}
	
//	@PostMapping("/aComment")
//	public String addActivitiesComment(@RequestBody ActivitiesComment aComment ) {
//		ActivitiesComment saveComment = commentService.saveComment(aComment);
//		if(saveComment!=null) {
//		return "add comment ok";
//		}
//		return "add comment not ok";	
//	}
	
//R----------------------------------------------------
//	@GetMapping("/activitiesComment/list")
//	public String findAll(Model model) {
//		List<ActivitiesComment> list = commentService.findAllComment();
//		model.addAttribute("commentList", list);
//		return "activities/showCommentPage";
//	}
//	@GetMapping("/aComment")
//	public List<ActivitiesComment> findAllaComment(){
//		List<ActivitiesComment> allComment = commentService.findAllComment();
//		return allComment;
//	}
//
//	@GetMapping("/aComment/{cid}")
//	public ActivitiesComment findById(@PathVariable("cid") Integer cid) {
//		ActivitiesComment commentById = commentService.findCommentById(cid);
//		if (commentById != null) {
//			return commentById;
//		}
//		return null;
//	}
	
	
//D-----------------------------------------------------
//	@GetMapping("/activitiesComment/delete")
//	public String deleteActComment(@RequestParam Integer id) {
//		commentService.deleteCommentById(id);
//		return "redirect:/activitiesComment/list";
//	}
	
//	@DeleteMapping("/aComment/{cid}")
//	public String deleteComment(@PathVariable("cid") Integer cid) {
//		boolean deleteMsg = commentService.deleteCommentById(cid);
//		if (deleteMsg) {
//			return "delete comment ok";
//		}
//		return "delete comment not ok";
//	}
	
	
//U--------------------------------------------------------
//	@GetMapping("/activitiesComment/update")
//	public String editActComment(@RequestParam Integer id, Model model, HttpSession httpSession) {
//		ActivitiesComment actComment = commentService.findCommentById(id);
//		model.addAttribute("actComment", actComment);
//		return "activities/editCommentPage";
//	}
//
//	@PostMapping("/activitiesComment/updatePost")
//	public String editActCommentPost(@ModelAttribute ActivitiesComment actComment) {
//		commentService.saveComment(actComment);
//
//		return "redirect:/activitiesComment/list";
//	}
	
//	@PutMapping("/aComment/{cid}")
//	public String editActComment(
//			@PathVariable("cid") Integer cid,
//			@RequestBody ActivitiesComment aComment) {
//		
//		ActivitiesComment commentById = commentService.findCommentById(cid);
////		commentById.setActivities(aComment.getActivities());
////		commentById.setUsers(aComment.getUsers());
//		commentById.setMessageContent(aComment.getMessageContent());
//		
//		commentService.saveComment(commentById);
//				return "update comment ok";
//		
//	}
//	
//	
//
//}

package com.meetu.controller;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.meetu.model.Activities;
import com.meetu.model.ActivitiesComment;
import com.meetu.model.Users;
import com.meetu.service.ActivitiesCommentService;
import com.meetu.service.ActivitiesService;
import com.meetu.service.UserService;
import com.meetu.util.JsonWebTokenUtility;

@RestController
@CrossOrigin
public class ActivitiesCommentController {

    @Autowired
    private ActivitiesCommentService commentService;

    @Autowired
    private ActivitiesService actService;

    @Autowired
    private UserService userService;

    @Autowired
    private JsonWebTokenUtility jsonWebTokenUtility;

    @PostMapping("/aComment")
    public ResponseEntity<String> addActivitiesComment(
            @RequestBody ActivitiesComment aComment,
            @RequestParam Integer activitiesId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or invalid");
        }

        try {
            String token = authHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);
            if (userData == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
            }
            JSONObject userJson = new JSONObject(userData);
            Integer userId = userJson.getInt("userId");

            Users users = new Users();
            users.setUserId(userId);
            aComment.setUsers(users);

            Activities activity = actService.findActById(activitiesId);
            if (activity == null) {
                return ResponseEntity.badRequest().body("Invalid activity ID");
            }
            aComment.setActivities(activity);

            aComment.setActivitiesReportStatus("N");

            ActivitiesComment savedComment = commentService.saveComment(aComment);
            if (savedComment != null) {
                return ResponseEntity.ok("Comment added successfully");
            }
            return ResponseEntity.badRequest().body("Failed to add comment");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/aComment")
    public List<ActivitiesComment> findAllActivitiesComment() {
        return commentService.findAllComment();
    }

    @GetMapping("/aComment/{cid}")
    public ResponseEntity<ActivitiesComment> findById(@PathVariable("cid") Integer cid) {
        ActivitiesComment commentById = commentService.findCommentById(cid);
        if (commentById != null) {
            return ResponseEntity.ok(commentById);
        }
        return ResponseEntity.notFound().build();
    }

//    @DeleteMapping("/aComment/{cid}")
//    public ResponseEntity<String> deleteComment(@PathVariable("cid") Integer cid) {
//        boolean deleteMsg = commentService.deleteCommentById(cid);
//        if (deleteMsg) {
//            return ResponseEntity.ok("Comment deleted successfully");
//        }
//        return ResponseEntity.badRequest().body("Failed to delete comment");
//    }
    @DeleteMapping("/aComment/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Integer commentId,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing or invalid");
        }

        try {
            String token = authHeader.substring(7);
            String userData = jsonWebTokenUtility.validateToken(token);
            if (userData == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token");
            }
            JSONObject userJson = new JSONObject(userData);
            Integer userId = userJson.getInt("userId");

            ActivitiesComment comment = commentService.findCommentById(commentId);
            if (comment == null) {
                return ResponseEntity.notFound().build();
            }

            // 檢查該留言是否屬於當前用戶
            if (!comment.getUsers().getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to delete this comment");
            }

            boolean isDeleted = commentService.deleteCommentById(commentId);
            if (isDeleted) {
                return ResponseEntity.ok("Comment deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete comment");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PutMapping("/aComment/{cid}")
    public ResponseEntity<String> editActivitiesComment(
            @PathVariable("cid") Integer cid,
            @RequestBody ActivitiesComment aComment) {
        
        ActivitiesComment commentById = commentService.findCommentById(cid);
        if (commentById == null) {
            return ResponseEntity.notFound().build();
        }
        
        commentById.setMessageContent(aComment.getMessageContent());
        
        ActivitiesComment updatedComment = commentService.saveComment(commentById);
        if (updatedComment != null) {
            return ResponseEntity.ok("Comment updated successfully");
        }
        return ResponseEntity.badRequest().body("Failed to update comment");
    }
}
