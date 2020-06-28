package org.gurenko.vladislav.tasklistwebservice.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.ToString;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class User {

    private Integer id;

    private String login;

    @ToString.Exclude
    private String password;

    private String firstName;

    private String lastName;
}

