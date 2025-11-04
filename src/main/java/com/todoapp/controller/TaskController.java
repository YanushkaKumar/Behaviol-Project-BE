package com.todoapp.controller;

import com.todoapp.dto.TaskRequest;
import com.todoapp.model.Task;
import com.todoapp.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        try {
            log.info("GET /api/todos - Fetching all tasks");
            List<Task> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            log.error("Error fetching tasks: ", e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest request) {
        try {
            log.info("POST /api/todos - Creating task: {}", request.getTitle() != null ? request.getTitle() : request.getText());
            Task task = taskService.createTask(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(task);
        } catch (Exception e) {
            log.error("Error creating task: ", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @PathVariable String id,
            @Valid @RequestBody TaskRequest request) {
        try {
            log.info("PUT /api/todos/{} - Updating task", id);
            Task task = taskService.updateTask(id, request);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            log.error("Error updating task {}: ", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Task> toggleTask(@PathVariable String id) {
        try {
            log.info("PATCH /api/todos/{}/toggle - Toggling task completion", id);
            Task task = taskService.toggleTask(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            log.error("Error toggling task {}: ", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<Task> archiveTask(@PathVariable String id) {
        try {
            log.info("PATCH /api/todos/{}/archive - Archiving task", id);
            Task task = taskService.archiveTask(id);
            return ResponseEntity.ok(task);
        } catch (RuntimeException e) {
            log.error("Error archiving task {}: ", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Boolean>> deleteTask(@PathVariable String id) {
        try {
            log.info("DELETE /api/todos/{} - Deleting task", id);
            taskService.deleteTask(id);
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error deleting task {}: ", id, e);
            Map<String, Boolean> response = new HashMap<>();
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    // Bulk operations endpoint (optional, for future use)
    @PostMapping("/bulk/complete")
    public ResponseEntity<Map<String, Object>> bulkComplete(@RequestBody Map<String, List<String>> request) {
        try {
            List<String> taskIds = request.get("taskIds");
            log.info("POST /api/todos/bulk/complete - Completing {} tasks", taskIds.size());

            int successCount = 0;
            int failCount = 0;

            for (String taskId : taskIds) {
                try {
                    taskService.toggleTask(taskId);
                    successCount++;
                } catch (Exception e) {
                    log.error("Error completing task {}: ", taskId, e);
                    failCount++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("successCount", successCount);
            response.put("failCount", failCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in bulk complete: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/bulk/delete")
    public ResponseEntity<Map<String, Object>> bulkDelete(@RequestBody Map<String, List<String>> request) {
        try {
            List<String> taskIds = request.get("taskIds");
            log.info("DELETE /api/todos/bulk/delete - Deleting {} tasks", taskIds.size());

            int successCount = 0;
            int failCount = 0;

            for (String taskId : taskIds) {
                try {
                    taskService.deleteTask(taskId);
                    successCount++;
                } catch (Exception e) {
                    log.error("Error deleting task {}: ", taskId, e);
                    failCount++;
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("successCount", successCount);
            response.put("failCount", failCount);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in bulk delete: ", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Task Service");
        return ResponseEntity.ok(response);
    }
}