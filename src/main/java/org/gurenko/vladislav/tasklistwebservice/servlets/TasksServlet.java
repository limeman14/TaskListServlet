package org.gurenko.vladislav.tasklistwebservice.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.gurenko.vladislav.tasklistwebservice.model.Task;
import org.gurenko.vladislav.tasklistwebservice.service.TaskService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@WebServlet(urlPatterns = "/tasks/*", name = "TasksServlet")
public class TasksServlet extends HttpServlet {

    private AtomicReference<ObjectMapper> objectMapper;

    @Override
    public void init() throws ServletException {
        objectMapper = new AtomicReference<>(
                new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .configure(SerializationFeature.INDENT_OUTPUT, true)
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        );
    }

    /*Получение задачи по id, возвращаем:
     - 200 - если все в порядке
     - 404 - если задание не найдено, или у пользователя нет к нему доступа
     - 400 - если запрос неверно составлен
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getSession().getAttribute("userId");
        String[] splits = req.getPathInfo().split("/");
        if(splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int taskId = Integer.parseInt(splits[1]);
        Task task = TaskService.getUserTaskById(taskId, userId);
        if (task.getTaskName() != null) {
            resp.setStatus(HttpServletResponse.SC_OK);
            sendAsJson(resp, task);
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    /*Добавление задания, возвращаем:
     - 201 - если все в порядке, задача создана
     - 404 - если задание не найдено, или у пользователя нет к нему доступа
     - 400 - если запрос неверно составлен
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            final String collect = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            final Task task = objectMapper.get().readValue(collect, Task.class);
            if (TaskService.addTask(task) == 1) {
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
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer userId = (Integer) req.getSession().getAttribute("userId");
        String[] splits = req.getPathInfo().split("/");
        if(splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        int taskId = Integer.parseInt(splits[1]);
    }

    //a utility method to send object
    //as JSON response
    private void sendAsJson(
            HttpServletResponse response,
            Object obj) throws IOException {

        response.setContentType("application/json; charset=UTF-8");

        String res = objectMapper.get().writeValueAsString(obj);

        PrintWriter out = response.getWriter();

        out.print(res);
        out.flush();
    }
}
