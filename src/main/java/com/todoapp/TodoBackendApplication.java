package com.todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class TodoBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoBackendApplication.class, args);
    }

}