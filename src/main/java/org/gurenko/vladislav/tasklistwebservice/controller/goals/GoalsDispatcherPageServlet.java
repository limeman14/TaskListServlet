package org.gurenko.vladislav.tasklistwebservice.controller.goals;

import org.gurenko.vladislav.tasklistwebservice.model.Goal;
import org.gurenko.vladislav.tasklistwebservice.repository.GoalRepo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/goalsPage", name = "GoalsDispatcherPageServlet")
public class GoalsDispatcherPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Integer userId = (Integer) req.getSession().getAttribute("userId");
        if (userId != null) {
            final List<Goal> goals = GoalRepo.getAllUserGoals(userId);
            req.setAttribute("goals", goals);
        }
        req.getRequestDispatcher("/view/goals.jsp").forward(req, resp);
    }
}
