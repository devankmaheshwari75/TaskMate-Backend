package com.joshi.services.admin;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joshi.repositories.CommentsRepository;
import com.joshi.repositories.TaskRepositries;
import com.joshi.repositories.UserRepository;
import com.joshi.services.EmailService;
import com.joshi.utils.JwtUtil;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.joshi.dto.CommentDTO;
import com.joshi.dto.TaskDto;
import com.joshi.dto.UserDto;
import com.joshi.entities.Comment;
import com.joshi.entities.Task;
import com.joshi.entities.User;
import com.joshi.enums.TaskStatus;
import com.joshi.enums.UserRole;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
	
	private final UserRepository userRepository;
	
	private final TaskRepositries taskRepositries;
	
	private final JwtUtil jwtUtil;
	
	private final CommentsRepository commentsRepository;
	
	private final EmailService emailService;
	
	

	@Override
	public List<UserDto> getUsers() {
		// TODO Auto-generated method stub
		return userRepository.findAll()
				.stream()
				.filter( user->user.getUserRole() ==UserRole.EMPLOYEE)
				.map(User::getUserDto).collect(Collectors.toList());	
	}

	@Override
	public TaskDto createTask(TaskDto taskDto) {
		Optional<User> optionaUser = userRepository.findById(taskDto.getEmployeeId());
		if(optionaUser.isPresent()) {
			Task task= new Task();
			task.setTitle(taskDto.getTitle());
			task.setDescription(taskDto.getDescription());
			task.setPriority(taskDto.getPriority());
			task.setDueDate(taskDto.getDueDate());
			task.setTaskStatus(TaskStatus.INPROGRESS);
			task.setUser(optionaUser.get());
			 TaskDto createdTask = taskRepositries.save(task).getTaskDto();
	            
	            // Send email notification
	            emailService.sendEmail(optionaUser.get().getEmail(), "Task Created", "A new task has been created for you: " + task.getTitle());

	            return createdTask;
			
		}
		return null;
	}

	@Override
	public List<TaskDto> getAllTasks() {
		
		return taskRepositries.findAll()
				.stream()
				.sorted(Comparator.comparing(Task::getDueDate).reversed())
				.map(Task::getTaskDto)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteTask(Long id) {
		taskRepositries.deleteById(id);
		
	}

	@Override
	public TaskDto getTaskById(Long id) {
		Optional<Task> optionalTask = taskRepositries.findById(id);
		
		return optionalTask.map(Task::getTaskDto).orElse(null);
	}
	
	/*
	@Override
	public TaskDto updateTask(Long id, TaskDto taskDto) {
		
		System.out.println("Updating task with id: " + id);  // Debug statement
	    System.out.println("Received taskDto: " + taskDto);  // Debug statement
	    
		Optional<Task> optionalTask = taskRepositries.findById(id);
		 
		if(optionalTask.isPresent()) {
			Task existingTask = optionalTask.get();
			existingTask.setTitle(taskDto.getTitle());
			existingTask.setDescription(taskDto.getDescription());
			existingTask.setDueDate(taskDto.getDueDate());
			existingTask.setPriority(taskDto.getPriority());
			existingTask.setTaskStatus(mapStringToTaskStatus(String.valueOf(taskDto.getTaskStatus())));
			
			return taskRepositries.save(existingTask).getTaskDto();
		}
		return null;
	}
	*/

	@Override
	public TaskDto updateTask(Long id, TaskDto taskDto) {
		
		System.out.println("Updating task with id: " + id);  // Debug statement
	    System.out.println("Received taskDto: " + taskDto);  // Debug statement
	    
		Optional<Task> optionalTask = taskRepositries.findById(id);
		Optional<User> optionalUser = userRepository.findById(taskDto.getEmployeeId()); 
		if(optionalTask.isPresent() && optionalUser.isPresent() ) {
			Task existingTask = optionalTask.get();
			existingTask.setTitle(taskDto.getTitle());
			existingTask.setDescription(taskDto.getDescription());
			existingTask.setDueDate(taskDto.getDueDate());
			existingTask.setPriority(taskDto.getPriority());
			existingTask.setTaskStatus(mapStringToTaskStatus(String.valueOf(taskDto.getTaskStatus())));
			existingTask.setUser(optionalUser.get());
			TaskDto updatedTask = taskRepositries.save(existingTask).getTaskDto();
            
            // Send email notification
            emailService.sendEmail(optionalUser.get().getEmail(), "Task Updated", "A task assigned to you has been updated: " + existingTask.getTitle());

            return updatedTask;
		}
		return null;
	}
	 
	
	@Override
	public List<TaskDto> searchTaskByTitle(String title) {
		return taskRepositries.findAllByTitleContaining(title)
				.stream()
				.sorted(Comparator.comparing(Task:: getDueDate).reversed())
				.map(Task:: getTaskDto)
				.collect(Collectors.toList());
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
	public List<CommentDTO> getCommentsByTaskId(Long taskId) {
		
		return commentsRepository.findAllByTaskId(taskId)
				.stream()
				.map(Comment:: getCommentDTO)
				.collect(Collectors.toList());
	}
	
	
	
	   // Scheduled method to send reminder emails
    @Scheduled(cron = "0 50 18 * * ?") // Run every day at 8 AM
    @Transactional
    public void sendTaskReminders() {
        Date tomorrow = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
        List<Task> tasksDueTomorrow = taskRepositries.findAll()
                .stream()
                .filter(task -> task.getDueDate() != null && isSameDay(tomorrow, task.getDueDate()))
                .collect(Collectors.toList());

        for (Task task : tasksDueTomorrow) {
            emailService.sendEmail(task.getUser().getEmail(), "Task Reminder", "Reminder: The task '" + task.getTitle() + "' is due tomorrow.");
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        return date1.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().equals(
                date2.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
    }
	
    
    
 
}
