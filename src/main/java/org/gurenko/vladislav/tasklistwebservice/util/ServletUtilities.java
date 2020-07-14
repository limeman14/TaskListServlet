package org.gurenko.vladislav.tasklistwebservice.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

public class ServletUtilities {
    /**
     * Метод для отправки ответа в формате JSON
     */
    public static void sendAsJson(ObjectMapper objectMapper,
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
    public static boolean checkPathInfo(String pathInfo) {
        return pathInfo == null || pathInfo.equals("/");
    }

    /**
     * Получает текст задания из запроса добавления/редактирования
     *
     */
    public static String getTextFromRequest(HttpServletRequest req) throws IOException {
        return req.getReader()
                .lines()
                .collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * Проверяет есть ли ошибка в запросе(более двух компонент в запросах get/put/delete)
     *
     * @throws IOException
     */
    public static boolean checkForBadRequest(HttpServletResponse resp, String[] splits) throws IOException {
        if (splits.length != 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return true;
        }
        return false;
    }
}
