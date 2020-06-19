package org.gurenko.vladislav.tasklistwebservice.servlets.auth;

import org.gurenko.vladislav.tasklistwebservice.model.User;
import org.gurenko.vladislav.tasklistwebservice.service.UserService;
import org.gurenko.vladislav.tasklistwebservice.util.PasswordAuthentication;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", urlPatterns = "/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = PasswordAuthentication.getHashSaltedPassword(req.getParameter("password"));
        String firstName = req.getParameter("first_name");
        String lastName = req.getParameter("last_name");

        User registeredUser = new User();
        registeredUser.setRole(User.UserRole.USER);
        registeredUser.setLogin(login);
        registeredUser.setPassword(password);
        registeredUser.setFirstName(firstName);
        registeredUser.setLastName(lastName);

        if (UserService.addUser(registeredUser) == 1) {
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
        else {
            req.setAttribute("message", "Пользователь существует или введены неверные параметры!");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
        }
    }
}
