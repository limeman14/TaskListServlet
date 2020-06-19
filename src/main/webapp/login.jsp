<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Страница входа</title>
</head>
<body>

<b><span style="font-size: large; color: red;">${requestScope.message}</span></b><br>

<div class="form">

    <h1>Вход в сервис задач</h1><br>
    <form method="post" action="${pageContext.request.contextPath}/login">

        <input type="text" required placeholder="login" name="login"><br>
        <input type="password" required placeholder="password" name="password"><br><br>
        <input class="button" type="submit" value="Войти"><br><br>
    </form>

</div>

<form>
    <input type="button" value="Регистрация" onClick='location.href="/register"'>
</form>
</body>
</html>
