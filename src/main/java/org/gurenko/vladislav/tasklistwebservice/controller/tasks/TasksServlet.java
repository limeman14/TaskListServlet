package org.gurenko.vladislav.tasklistwebservice.controller.tasks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.gurenko.vladislav.tasklistwebservice.model.Task;
import org.gurenko.vladislav.tasklistwebservice.repository.TaskRepo;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@WebServlet(urlPatterns = "/tasks/*", name = "TasksServlet")
public class TasksServlet extends HttpServlet {

    private AtomicReference<ObjectMapper> objectMapper;

    /**
     * Инициализация Jackson Mapper с красивым форматированием JSON
     * и правильной сериализацией/десериализацией даты-времени
     * @throws ServletException
     */

    @Override
    public void init() throws ServletException {
        objectMapper = new AtomicReference<>(
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .configure(SerializationFeature.INDENT_OUTPUT, true)
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        );
    }

    /**
     * Получение задачи по id ИЛИ получение всех задач, возвращаем:
     * - 200 - если все в порядке
     * - 404 - если задание не найдено, или у пользователя нет к нему доступа
     * - 400 - если запрос неверно составлен
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getSession().getAttribute("userId");
        String pathInfo = req.getPathInfo();
        if(checkPathInfo(pathInfo)){           //если нет числа после слеша в url - отправляем все задания пользователя
            List<Task> tasks = TaskRepo.getUserAllTasks(userId);
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, tasks);
            return;
        }
        String[] splits = pathInfo.split("/");

        if (sendBadRequestError(resp, splits)) return;
        int taskId = Integer.parseInt(splits[1]);
        Task task = TaskRepo.getUserTaskById(taskId, userId);
        if (task.getTaskName() != null) {                       //если задание по id было найдено - отправляем его одно
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, task);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /**Добавление задания, возвращаем:
     * - 201 - если все в порядке, задача создана
     * - 404 - если задание не найдено, или у пользователя нет к нему доступа
     * - 400 - если запрос неверно составлен
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");

        if (checkPathInfo(pathInfo)) {
            final String collect = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            System.out.println(collect);
            final Task task = objectMapper.get().readValue(collect, Task.class);
            if (TaskRepo.addTask(task) == 1) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                sendAsJson(resp, "ok");
            }
        }
        else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        req.setCharacterEncoding("UTF-8");

        if (checkPathInfo(pathInfo)) {
            String[] splits = pathInfo.split("/");
            if (sendBadRequestError(resp, splits)) return;

        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getSession().getAttribute("userId");
        String[] splits = req.getPathInfo().split("/");
        if (sendBadRequestError(resp, splits)) return;
        int taskId = Integer.parseInt(splits[1]);
    }

    /**
     * Метод для отправки ответа в формате JSON
     */
    private void sendAsJson(
            HttpServletResponse response,
            Object obj) throws IOException {

        response.setContentType("application/json; charset=UTF-8");

        String res = objectMapper.get().writeValueAsString(obj);

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
