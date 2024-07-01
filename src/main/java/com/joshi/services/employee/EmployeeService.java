package com.joshi.services.employee;

import java.util.List;
import java.util.Map;

import com.joshi.dto.CommentDTO;
import com.joshi.dto.TaskDto;

public interface EmployeeService {
		List<TaskDto> getTaskByUserId();
		
	  TaskDto updateTask(Long id, String status);

	TaskDto getTaskById(Long id);
    
    CommentDTO createComment(Long taskId, String content);
    
   List<CommentDTO> getCommentsByTaskId(Long taskId);

//Map<String, Long> getTaskCountByPriorityForEmployee();

//Map<String, Long> getTaskCountByStatusForEmployee();

}
