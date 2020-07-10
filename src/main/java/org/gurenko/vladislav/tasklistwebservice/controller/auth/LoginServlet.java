package org.gurenko.vladislav.tasklistwebservice.controller.auth;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gurenko.vladislav.tasklistwebservice.model.User;
import org.gurenko.vladislav.tasklistwebservice.repository.UserRepo;
import org.gurenko.vladislav.tasklistwebservice.util.PasswordAuthentication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private final Logger logger = LogManager.getLogger(LoginServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().getAttribute("login") != null) {
            resp.sendRedirect("/tasksPage");
            return;
        }
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String userName = req.getParameter("login");
        final User user = UserRepo.getUserByLogin(userName);
        final String storedPassword = user.getPassword();

        final boolean loginSuccess = user.getLogin() != null &&
                user.getLogin().equals(userName) &&
                PasswordAuthentication.checkPasswords(req.getParameter("password"), storedPassword);
        if (loginSuccess) {
            final HttpSession session = req.getSession();
            final int userId = user.getId();
            session.setAttribute("login", userName);
            session.setAttribute("name", user.getFirstName());
            session.setAttribute("surname", user.getLastName());
            session.setAttribute("userId", userId);
            logger.info("User with email " + userName + " logged in.");
            resp.sendRedirect("/tasksPage");
        } else {
            req.setAttribute("message", "Неверный логин/пароль");
            logger.warn("Access denied: wrong password or login from user: " + userName);
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }
}
