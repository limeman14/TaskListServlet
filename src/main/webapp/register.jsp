<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<html>
<head>
    <title>Регистрация в сервисе</title>
</head>
<body>



<div class="form">

    <h1>Регистрация в сервисе задач</h1><br>
    <b><span style="font-size: large; color: red;">${requestScope.message}</span></b><br>

    <form method="post" action="${pageContext.request.contextPath}/register">

        <input type="text" required placeholder="Введите логин" name="login"><br>
        <input type="password" required placeholder="Введите пароль" name="password"><br>
        <input type="text" required placeholder="Введите имя" name="first_name"><br>
        <input type="text" required placeholder="Введите фамилию" name="last_name"><br>
        <input class="button" type="submit" value="Зарегистрироваться"><br><br>

    </form>
</div>

<form>
    <input type="button" value="Вернуться к логину" onClick='location.href="/login"'>
</form>
</body>
</html>
