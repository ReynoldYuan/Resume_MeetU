package com.meetu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.meetu.model.ActivitiesComment;
import com.meetu.model.ActivitiesCommentRepository;
import com.meetu.model.Users;

@Service
public class ActivitiesCommentService {

	@Autowired
	private ActivitiesCommentRepository commentRepo;

	public ActivitiesComment saveComment(ActivitiesComment aComment) {
		return commentRepo.save(aComment);
	}

	public ActivitiesComment findCommentById(Integer id) {
		Optional<ActivitiesComment> optional = commentRepo.findById(id);

		if (optional.isPresent()) {
			ActivitiesComment result = optional.get();
			return result;
		}

		return null;

	}

	public List<ActivitiesComment> findAllComment() {
		return commentRepo.findAll();
	}

	public boolean deleteCommentById(Integer id) {

		if (commentRepo.existsById(id)) {
			commentRepo.deleteById(id);
			return true;
		}
		return false;
	}

	public List<ActivitiesComment> findCommentsByActivityId(Integer aid) {
		return commentRepo.findByActivityIdAndReportStatusN(aid);
	}

	
	// 更新活動留言檢舉狀態
	public ActivitiesComment updateActivitiesComment(ActivitiesComment activitiesComment) {
		if (activitiesComment == null || activitiesComment.getActivitiesCommentId() == null) {
			throw new IllegalArgumentException("使用者或使用者ID不能為空");
		}

		ActivitiesComment existingActivitiesComment = commentRepo.findById(activitiesComment.getActivitiesCommentId())
				.orElse(null);
		if (existingActivitiesComment == null) {
			throw new IllegalArgumentException("找不到指定ID的使用者");
		}

		existingActivitiesComment.setActivitiesReportStatus(activitiesComment.getActivitiesReportStatus());
		return commentRepo.save(existingActivitiesComment);
	}

}
