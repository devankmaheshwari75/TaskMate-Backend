package com.joshi.services.admin;

import java.util.List;
import java.util.Map;

import com.joshi.dto.CommentDTO;
import com.joshi.dto.TaskDto;
import com.joshi.dto.UserDto;

public interface AdminService {
	
	List<UserDto> getUsers();
	TaskDto  createTask(TaskDto taskDto);
	
	List<TaskDto> getAllTasks();
	
    void deleteTask(Long id);   
   
    
    TaskDto updateTask(Long id , TaskDto taskDto) ;
    
    
    
    
    List<TaskDto> searchTaskByTitle(String title);
    
    TaskDto getTaskById(Long id);
    
    CommentDTO createComment(Long taskId, String content);
    
   List<CommentDTO> getCommentsByTaskId(Long taskId);
   	 
}
