package org.gurenko.vladislav.tasklistwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Task {
    @JsonProperty("task_id")
    private Integer id;

    private String taskName;

    private String description;

    private LocalDateTime dueDate;

    private Boolean isDone;

    private Integer creatorId;

    private Integer goalId;
}
