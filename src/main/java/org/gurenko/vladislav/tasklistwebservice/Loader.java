package org.gurenko.vladislav.tasklistwebservice;

import org.gurenko.vladislav.tasklistwebservice.model.Task;
import org.gurenko.vladislav.tasklistwebservice.service.TaskService;
import org.gurenko.vladislav.tasklistwebservice.service.UserService;
import org.gurenko.vladislav.tasklistwebservice.model.User;
import org.gurenko.vladislav.tasklistwebservice.util.PasswordAuthentication;

import java.time.LocalDateTime;
import java.util.List;

public class Loader {
    public static void main(String[] args) {
        User user = UserService.getUserByLogin("123shit");
        System.out.println(user);


        Task oldTask = TaskService.getUserTaskById(10, 1);
        Task newTask = TaskService.editTask(10, new Task("fuck you", "newdescrip", LocalDateTime.now(), false, 1));
        System.out.println(oldTask + "\n" + newTask);

        List<? extends Task> tasks = TaskService.getUserAllTasks(1);
        tasks.forEach(System.out::println);

        final String warofwar = PasswordAuthentication.getHashSaltedPassword("123456789");
        if (PasswordAuthentication.checkPasswords("123456789", warofwar)) {
            System.out.println(false);
            System.out.println(warofwar);
        }

    }
}