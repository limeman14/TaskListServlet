package org.gurenko.vladislav.tasklistwebservice.controller.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.gurenko.vladislav.tasklistwebservice.model.Task;
import org.gurenko.vladislav.tasklistwebservice.repository.GoalRepo;
import org.gurenko.vladislav.tasklistwebservice.repository.TaskRepo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;


@WebServlet(urlPatterns = "/tasks/*", name = "TasksServlet")
public class TasksServlet extends HttpServlet {

    private ObjectMapper objectMapper;
    private final Logger logger = LogManager.getLogger(TasksServlet.class);
    /**
     * Инициализация Jackson Mapper с "красивым" форматированием JSON
     * и правильной сериализацией/десериализацией даты-времени
     *
     */
    @Override
    public void init() {
        objectMapper = new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .configure(SerializationFeature.INDENT_OUTPUT, true)
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * Получение задачи по id ИЛИ получение всех задач, возвращаем:
     * - 200 - если все в порядке
     * - 404 - если задание не найдено, или у пользователя нет к нему доступа
     * - 400 - если запрос неверно составлен
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Integer userId = (Integer) req.getSession().getAttribute("userId");
        final String pathInfo = req.getPathInfo();
        if(checkPathInfo(pathInfo)){           //если нет числа после слеша в url - отправляем все задания пользователя
            List<Task> tasks = TaskRepo.getUserAllTasks(userId);
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, tasks);
            logger.info("For user with id " + userId + " found " + tasks.size() + " tasks.");
            return;
        }
        final String[] splits = pathInfo.split("/");

        if (sendBadRequestError(resp, splits)) return;
        final int taskId = Integer.parseInt(splits[1]);
        final Task task = TaskRepo.getUserTaskById(taskId, userId);
        if (task.getTaskName() != null) {                       //если задание по id было найдено - отправляем его одно
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, task);
            logger.info("Task with id " + taskId + " found.");
            return;
        }
        logger.warn("Tasks for user " + userId + " were not found.");
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**
     * Добавление задания, возвращаем:
     * - 201 - если все в порядке, задача создана
     * - 400 - если запрос неверно составлен
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");

        if (checkPathInfo(pathInfo)) {
            final String collect = getTextFromRequest(req);
            final Task task = objectMapper.readValue(collect, Task.class);
            if (TaskRepo.addTask(task) == 1) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                sendAsJson(resp, "ok");
                logger.info("Task with name " + task.getTaskName() + " successfully created for user with id " + req.getSession().getAttribute("userId"));
            }
        }
        else {
            logger.warn("Wrong task request by user with id " + req.getSession().getAttribute("userId"));
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Редактирование задачи по id
     * - 200 - если все в порядке, задание отредактирована
     * - 404 - если задание не найдено, или у пользователя нет к нему доступа
     * - 400 - если запрос неверно составлен
     */

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");

        final String[] splits = pathInfo.split("/");
        if (sendBadRequestError(resp, splits)) return;
        final String newTaskText = getTextFromRequest(req);
        Task newTask = objectMapper.readValue(newTaskText, Task.class);

        //Проверка на то, существует ли указанная в теле запроса цель или нет, если её нет - устанавливаем дефолтную цель с номером 0
        if (GoalRepo.getUserGoalById(newTask.getGoalId(), newTask.getCreatorId()) == null) {
            newTask.setGoalId(0);
        }

        final int taskId = Integer.parseInt(splits[1]);
        final Task editedTask = TaskRepo.editTask(taskId, newTask);
        if (editedTask.getTaskName() != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, editedTask);
            logger.info("Task with id " + taskId + " was successfully changed.");
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.warn("Task with id " + taskId + " was not found.");
        }
    }

    /**
     * Удаление задачи по id
     * - 200 - если все в порядке, задание удалено
     * - 404 - если задание не найдено, или у пользователя нет к нему доступа
     * - 400 - если запрос неверно составлен
     */

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String[] splits = req.getPathInfo().split("/");
        if (sendBadRequestError(resp, splits)) return;
        final int taskId = Integer.parseInt(splits[1]);
        if (TaskRepo.deleteTask(taskId) == 1) {
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, "Задание успешно удалено");
            logger.info("Task with " + taskId + " was successfully deleted.");
        }
        else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            logger.warn("Task with id " + taskId + " was not found.");
        }
    }

    /**
     * Метод для отправки ответа в формате JSON
     *
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
     * Получает текст задания из запроса добавления/редактирования
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
     * @throws IOException
     */
    private boolean sendBadRequestError(HttpServletResponse resp, String[] splits) throws IOException {
        if (splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return true;
        }
        return false;
    }
}
