package org.gurenko.vladislav.tasklistwebservice.servlets.auth;

import org.gurenko.vladislav.tasklistwebservice.model.Task;
import org.gurenko.vladislav.tasklistwebservice.model.User;
import org.gurenko.vladislav.tasklistwebservice.service.TaskService;
import org.gurenko.vladislav.tasklistwebservice.service.UserService;
import org.gurenko.vladislav.tasklistwebservice.util.PasswordAuthentication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().getAttribute("login") != null) {
            resp.sendRedirect("/");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String userName = req.getParameter("login");
        final User user = UserService.getUserByLogin(userName);
        final String storedPassword = user.getPassword();

        final boolean loginSuccess = user.getLogin() != null &&
                user.getLogin().equals(userName) &&
                PasswordAuthentication.checkPasswords(req.getParameter("password"), storedPassword);
        if (loginSuccess) {
            final HttpSession session = req.getSession();
            final int userId = user.getId();
            session.setAttribute("login", userName);
            session.setAttribute("name", user.getFirstName());
            session.setAttribute("userId", userId);
            final List<Task> tasks = TaskService.getUserAllTasks(userId);
            session.setAttribute("tasks", tasks);
            resp.sendRedirect("/");
        } else {
            req.setAttribute("message", "Неверный логин/пароль");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
