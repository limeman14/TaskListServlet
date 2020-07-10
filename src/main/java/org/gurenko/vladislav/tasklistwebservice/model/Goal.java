package org.gurenko.vladislav.tasklistwebservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonPropertyOrder({"goal_id", "description", "parent_goal", "sub_goals_counter", "creator_id", "assigned_tasks"})
public class Goal {

    @JsonProperty("goal_id")
    private Integer id;

    private Integer parentGoal;

    private String description;

    private Integer creatorId;

    private List<Task> assignedTasks;

    private Integer subGoalsCounter;
}
