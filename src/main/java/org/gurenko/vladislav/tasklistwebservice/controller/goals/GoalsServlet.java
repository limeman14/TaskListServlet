package org.gurenko.vladislav.tasklistwebservice.controller.goals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gurenko.vladislav.tasklistwebservice.model.Goal;
import org.gurenko.vladislav.tasklistwebservice.model.dto.AddGoalDto;
import org.gurenko.vladislav.tasklistwebservice.model.dto.AddParentGoalDto;
import org.gurenko.vladislav.tasklistwebservice.repository.GoalRepo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/goals/*", name = "GoalsServlet")
public class GoalsServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private final Logger logger = LogManager.getLogger(GoalsServlet.class);

    @Override
    public void init() {
        objectMapper = new ObjectMapper()
                .configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    /**
     * Получение цели по id ИЛИ получение всех целей у пользователя, возвращаем:
     * - 200 - если все в порядке
     * - 404 - если цель не найдена, или у пользователя нет к ней доступа
     * - 400 - если запрос неверно составлен
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Integer userId = (Integer) req.getSession().getAttribute("userId");
        final String pathInfo = req.getPathInfo();
        if(checkPathInfo(pathInfo)){           //если нет числа после слеша в url - отправляем все цели пользователя
            final List<Goal> goals = GoalRepo.getAllUserGoals(userId);
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, goals);
            logger.info("For user with id " + userId + " found " + goals.size() + " goals.");
            return;
        }
        final String[] splits = pathInfo.split("/");
        if (sendBadRequestError(resp, splits)) return;
        final int goalId = Integer.parseInt(splits[1]);
        final Goal goal = GoalRepo.getUserGoalById(goalId, userId);
        if (goal != null) {                       //если цель по id была найдена - отправляем её одну
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, goal);
            logger.info("Goal with id " + goalId + " found.");
            return;
        }
        logger.warn("Goals for user " + userId + " were not found.");
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Добавление цели, возвращаем:
     * - 201 - если все в порядке, цель создана
     * - 400 - если запрос неверно составлен
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");

        if (checkPathInfo(pathInfo)) {
            final String collect = getTextFromRequest(req);
            final AddGoalDto addGoalDto = objectMapper.readValue(collect, AddGoalDto.class);
            final Goal goal = Goal.builder()
                    .description(addGoalDto.getDescription())
                    .creatorId(addGoalDto.getCreatorId())
                    .build();
            if (GoalRepo.addGoal(goal) == 1) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                sendAsJson(resp, "Цель успешно создана");
                logger.info("Goal with name " + goal.getDescription() + " successfully created for user with id " + req.getSession().getAttribute("userId"));
            }
        }
        else {
            logger.warn("Wrong goal request by user with id " + req.getSession().getAttribute("userId"));
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Удаление цели по id
     * - 200 - если все в порядке, цель удалена
     * - 404 - если цель не найдена
     * - 400 - если запрос неверно составлен
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String[] splits = req.getPathInfo().split("/");
        if (sendBadRequestError(resp, splits)) return;
        final int goalId = Integer.parseInt(splits[1]);
        if (GoalRepo.deleteGoal(goalId) == 1) {
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, "Цель успешно удалена, все задания связанные с ней теперь не относятся к какой-либо цели!");
            logger.info("Goal with " + goalId + " was successfully deleted.");
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.warn("Goal with id " + goalId + " was not found.");
        }
    }

    /**
     * Добавление/удаление подцели у существующей цели, метод ПРОВЕРЯЕТ существует ли добавляемая подцель
     * Для того, чтобы удалить подцель, необходимо установить значение goalId = 0
     * - 200 - если все в порядке, цель отредактирована
     * - 404 - если цель не найдена
     * - 400 - если запрос неверно составлен
     */

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String[] splits = req.getPathInfo().split("/");
        if (sendBadRequestError(resp, splits)) return;
        final int goalId = Integer.parseInt(splits[1]);
        final String content = getTextFromRequest(req);
        final int parentGoalId = objectMapper.readValue(content, AddParentGoalDto.class).getParentGoalId();
        if ((parentGoalId == 0 ||                                                               //Если хотим убрать родительскую цель, устанавливаем 0
                GoalRepo.getUserGoalById(                                                   //Проверка существования подцели в базе данных
                parentGoalId,
                (Integer) req.getSession().getAttribute("userId")) != null) &&
                GoalRepo.addOrRemoveParentGoal(parentGoalId, goalId) == 1)                                //Добавление/удаление подцели
        {
                resp.setStatus(HttpServletResponse.SC_OK);
                sendAsJson(resp, "Главная цель успешно добавлена ИЛИ удалена!");
                logger.info("Added parent goal with id " + parentGoalId + " to goal with id " + goalId);
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.warn("Parent goal with id " + parentGoalId + " was not found.");
        }
    }

    /**
     * Отправляет ответ в виде JSON
     */
    private void sendAsJson(
            HttpServletResponse response,
            Object obj) throws IOException {
        response.setHeader("Content-Type", "application/json; charset=UTF-8");
        String res = objectMapper.writeValueAsString(obj);
        PrintWriter out = response.getWriter();
        out.print(res);
        out.flush();
    }

    /**
     * Проверяет, существует ли указанный путь без последующих идентификаторов
     *
     */
    private boolean checkPathInfo(String pathInfo) {
        return pathInfo == null || pathInfo.equals("/");
    }

    /**
     * Получает текст цели из запроса добавления/редактирования
     *
     */
    private String getTextFromRequest(HttpServletRequest req) throws IOException {
        return req.getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Проверяет есть ли ошибка в запросе(более двух компонент в запросах get/put/delete)
     *
     *
     */
    private boolean sendBadRequestError(HttpServletResponse resp, String[] splits) throws IOException {
        if (splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return true;
        }
        return false;
    }
}
