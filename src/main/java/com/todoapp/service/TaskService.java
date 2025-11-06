package com.todoapp.service;

import com.todoapp.dto.TaskRequest;
import com.todoapp.model.Task;
import com.todoapp.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        throw new RuntimeException("User not authenticated");
    }

    public List<Task> getAllTasks() {
        String userId = getCurrentUserId();
        log.info("Fetching all tasks for user: {}", userId);
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Task createTask(TaskRequest request) {
        String userId = getCurrentUserId();
        log.info("Creating task for user: {}", userId);

        Task task = new Task();
        task.setUserId(userId);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : "medium");
        task.setTags(request.getTags());
        task.setDueDate(LocalDate.from(request.getDueDate()));
        task.setCompleted(false);
        task.setArchived(false);
        task.setCreatedAt(Instant.now());

        Task savedTask = taskRepository.save(task);
        log.info("Task created with id: {}", savedTask.getId());
        return savedTask;
    }

    public Task updateTask(String taskId, TaskRequest request) {
        String userId = getCurrentUserId();
        log.info("Updating task {} for user: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Verify the task belongs to the current user
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this task");
        }

        // Update fields if provided
        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            task.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }

        if (request.getTags() != null) {
            task.setTags(request.getTags());
        }

        if (request.getDueDate() != null) {
            task.setDueDate(LocalDate.from(request.getDueDate()));
        }

        if (request.getCompleted() != null) {
            task.setCompleted(request.getCompleted());
        }

        if (request.getArchived() != null) {
            task.setArchived(request.getArchived());
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated: {}", taskId);
        return updatedTask;
    }

    public Task toggleTask(String taskId) {
        String userId = getCurrentUserId();
        log.info("Toggling task {} for user: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Verify the task belongs to the current user
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to toggle this task");
        }

        task.setCompleted(!task.isCompleted());
        Task updatedTask = taskRepository.save(task);
        log.info("Task toggled: {} - completed: {}", taskId, updatedTask.isCompleted());
        return updatedTask;
    }

    public void deleteTask(String taskId) {
        String userId = getCurrentUserId();
        log.info("Deleting task {} for user: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Verify the task belongs to the current user
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to delete this task");
        }

        taskRepository.delete(task);
        log.info("Task deleted: {}", taskId);
    }

    public Task archiveTask(String taskId) {
        String userId = getCurrentUserId();
        log.info("Archiving task {} for user: {}", taskId, userId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // Verify the task belongs to the current user
        if (!task.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized to archive this task");
        }

        task.setArchived(!task.isArchived());
        Task updatedTask = taskRepository.save(task);
        log.info("Task archived: {} - archived: {}", taskId, updatedTask.isArchived());
        return updatedTask;
    }
}