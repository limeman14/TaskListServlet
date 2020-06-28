package org.gurenko.vladislav.tasklistwebservice.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Task {
    private Integer id;

    private String taskName;

    private String description;

    private LocalDateTime dueDate;

    private Boolean isDone;

    private Integer creatorId;

    public Task(String taskName, String description, LocalDateTime dueDate, Boolean done, Integer creatorId) {
        this.taskName = taskName;
        this.description = description;
        this.dueDate = dueDate;
        this.isDone = done;
        this.creatorId = creatorId;
    }
}
