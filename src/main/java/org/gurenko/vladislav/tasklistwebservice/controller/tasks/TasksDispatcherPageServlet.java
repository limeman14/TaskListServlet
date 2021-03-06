package org.gurenko.vladislav.tasklistwebservice.controller.tasks;

import org.gurenko.vladislav.tasklistwebservice.model.Task;
import org.gurenko.vladislav.tasklistwebservice.repository.TaskRepo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@WebServlet(urlPatterns = "/tasksPage", name = "TasksDispatcherPageServlet")
public class TasksDispatcherPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Integer userId = (Integer) req.getSession().getAttribute("userId");
        if (userId != null) {
            final List<Task> tasks = TaskRepo.getUserAllTasks(userId);
            req.setAttribute("tasks", tasks);
        }
        req.getRequestDispatcher("/view/tasks.jsp").forward(req, resp);
    }
}
