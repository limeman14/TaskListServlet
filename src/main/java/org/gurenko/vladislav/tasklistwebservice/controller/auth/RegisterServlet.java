package org.gurenko.vladislav.tasklistwebservice.controller.auth;

import org.gurenko.vladislav.tasklistwebservice.model.User;
import org.gurenko.vladislav.tasklistwebservice.repository.UserRepo;
import org.gurenko.vladislav.tasklistwebservice.util.PasswordAuthentication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    private static final String EMAIL_REGEX = "^(?i)[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        if (!login.matches(EMAIL_REGEX)) {
            resp.sendError(400, "Неверный формат логина - укажите e-mail");
            return;
        }
        String password = PasswordAuthentication.getHashSaltedPassword(req.getParameter("password"));
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");

        User registeredUser = new User();
        registeredUser.setLogin(login);
        registeredUser.setPassword(password);
        registeredUser.setFirstName(firstName);
        registeredUser.setLastName(lastName);

        if (UserRepo.addUser(registeredUser) == 1) {
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
        else {
            req.setAttribute("message", "Пользователь существует или введены неверные параметры!");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
