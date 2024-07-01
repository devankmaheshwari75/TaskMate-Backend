package com.joshi.controller.employee;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.joshi.dto.CommentDTO;
import com.joshi.dto.TaskDto;
import com.joshi.services.admin.AdminService;
import com.joshi.services.employee.EmployeeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EmployeeController {
	
	private final EmployeeService employeeService;
	//private final AdminService adminService;
	
	@GetMapping("/tasks")
	public ResponseEntity<List<TaskDto>> getTaskByUserId(){
		return ResponseEntity.ok(employeeService.getTaskByUserId());
	}
	
	
	@GetMapping("/task/{id}/{status}")	
	public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, @PathVariable String status){
		TaskDto updatedTaskDTO =  employeeService.updateTask(id, status);
		if( updatedTaskDTO  == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		return ResponseEntity.ok(updatedTaskDTO);
	}
	
	
	

	@GetMapping("/task/{id}")
	public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id){
		  return ResponseEntity.ok(employeeService.getTaskById(id)) ;
	}



	@PostMapping("/task/comment/{taskId}")
	public ResponseEntity<CommentDTO> createComment(@PathVariable Long taskId, @RequestParam  String content){
	 CommentDTO createtedCommentDTO = 	employeeService.createComment(taskId, content);
	 
	 if(createtedCommentDTO ==null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	 
	 return ResponseEntity.status(HttpStatus.CREATED).body(createtedCommentDTO);
	}

	@GetMapping("/comments/{taskId}")
	public ResponseEntity< List<CommentDTO>> getCommentByTaskId(@PathVariable Long taskId) {
		return ResponseEntity.ok(employeeService.getCommentsByTaskId(taskId));
	}
	
	
	
	
	
}
