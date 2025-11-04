package com.todoapp.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskRequest {
    @NotBlank(message = "Task title is required")
    private String title;

    private String description;

    private String priority = "medium";

    private List<String> tags;

    private LocalDateTime dueDate;

    private Boolean completed;

    private Boolean archived;

    // Backward compatibility - frontend might send 'text' instead of 'title'
    public void setText(String text) {
        this.title = text;
    }

    public String getText() {
        return this.title;
    }
}