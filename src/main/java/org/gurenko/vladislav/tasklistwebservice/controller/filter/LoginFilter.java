package org.gurenko.vladislav.tasklistwebservice.controller.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")    //Все страницы сайта
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //получение данных сессии
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        //URL Запроса/переадресации на Servlet входа
        String loginURI = request.getContextPath() + "/login";
        String registerURI = request.getContextPath() + "/register";
        //Если сессия ранее создана
        boolean loggedIn = session != null && session.getAttribute("login") != null;
        boolean loginAndRegisterRequest = request.getRequestURI().equals(loginURI) || request.getRequestURI().equals(registerURI);
        //Если запрос пришел со страницы с входом или сессия не пуста даем добро следовать дальше
        //Если нет  - редирект на страницу входа
        if (loggedIn || loginAndRegisterRequest) {
            filterChain.doFilter(request, response);
        } else {
            response.sendRedirect(loginURI);
        }
    }

    @Override
    public void destroy() {

    }
}
