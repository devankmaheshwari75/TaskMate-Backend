package com.joshi.services.employee;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.joshi.dto.CommentDTO;
import com.joshi.dto.TaskDto;
import com.joshi.entities.Comment;
import com.joshi.entities.Task;
import com.joshi.entities.User;
import com.joshi.enums.TaskStatus;
import com.joshi.repositories.CommentsRepository;
import com.joshi.repositories.TaskRepositries;
import com.joshi.services.EmailService;
import com.joshi.utils.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{
	
	private static final String ADMIN_EMAIL = "mukulcoecgc@gmail.com";
	
	private final TaskRepositries taskRepositries;
	
	private final JwtUtil jwtUtil;
	
	private final CommentsRepository commentsRepository;
	
	//private final EmployeeService employeeService;
	 private final EmailService emailService;
	 
	 

	@Override
	public List<TaskDto> getTaskByUserId() {
	  User user = 	jwtUtil.getLoggedInUser();
	  if(user!=null) {
		  return taskRepositries.findAllByUserId(user.getId())
		   .stream()
		  .sorted(Comparator.comparing(Task:: getDueDate).reversed())
		  .map(Task::getTaskDto)
		  .collect(Collectors.toList());
	  }
		
		throw new EntityNotFoundException("User not found");
	}
	
	 @Override
	    public TaskDto updateTask(Long id, String status) {
	        Optional<Task> optionalTask = taskRepositries.findById(id);

	        if (optionalTask.isPresent()) {
	            Task existingTask = optionalTask.get();
	            existingTask.setTaskStatus(mapStringToTaskStatus(status));
	            TaskDto updatedTask = taskRepositries.save(existingTask).getTaskDto();

	            // Send email notification to admin
	            emailService.sendEmail(ADMIN_EMAIL, "Task Updated", 
	                    "The task '" + existingTask.getTitle() + "' has been updated by " + existingTask.getUser().getName() + ".");

	            return updatedTask;
	        }

	        throw new EntityNotFoundException("Task Not Found!!");
	    }

	private TaskStatus mapStringToTaskStatus(String status) {
		return switch(status) {
		case "PENDING" -> TaskStatus.PENDING;
		case "INPROGRESS" -> TaskStatus.INPROGRESS;
		case "COMPLETED" ->TaskStatus.COMPLETED;
		case "DEFERRED" ->TaskStatus.DEFERRED;
		default ->TaskStatus.CANCELLED;	
		
		};
	}

	@Override
	public TaskDto getTaskById(Long id) {
		Optional<Task> optionalTask = taskRepositries.findById(id);
		
		return optionalTask.map(Task::getTaskDto).orElse(null);
	}

@Override
	public CommentDTO createComment(Long taskId, String content) {
	  Optional<Task> optionalTask= 	taskRepositries.findById(taskId);
	  
	  User user = jwtUtil.getLoggedInUser();
	  
	  if((optionalTask.isPresent()) && user !=null) {
		  Comment comment = new Comment();
		  comment.setCreatedAt(new Date());
		  comment.setContent(content);
		  comment.setTask(optionalTask.get());
		  comment.setUser(user);
		  return commentsRepository.save(comment).getCommentDTO();
		 
		  
	  }
		throw new EntityNotFoundException("User or task Not Found");
	}



@Override
	public List<CommentDTO> getCommentsByTaskId(Long taskId) {
		
		return commentsRepository.findAllByTaskId(taskId)
				.stream()
				.map(Comment:: getCommentDTO)
				.collect(Collectors.toList());
	}

/*
@Override
public Map<String, Long> getTaskCountByPriorityForEmployee() {
    User user = jwtUtil.getLoggedInUser();
    if (user != null) {
        // Retrieve task counts by priority for the logged-in employee
        Map<String, Long> taskCountByPriority = taskRepositries.countTasksByPriorityForUser(user.getId());
        return taskCountByPriority;
    }
    throw new EntityNotFoundException("User not found");
}

@Override
public Map<String, Long> getTaskCountByStatusForEmployee() {
    User user = jwtUtil.getLoggedInUser();
    if (user != null) {
        // Retrieve task counts by status for the logged-in employee
        Map<String, Long> taskCountByStatus = taskRepositries.countTasksByStatusForUser(user.getId());
        return taskCountByStatus;
    }
    throw new EntityNotFoundException("User not found");
}
	*/
}
