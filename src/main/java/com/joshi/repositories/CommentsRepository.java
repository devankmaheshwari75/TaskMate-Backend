package com.joshi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.joshi.dto.CommentDTO;
import com.joshi.entities.Comment;

@Repository
public interface CommentsRepository  extends JpaRepository<Comment, Long>{

	List<Comment> findAllByTaskId(Long taskId);
	
	

}
