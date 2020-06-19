<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Добро пожаловать в сервис по управлению задачами</title>
    <script><%@include file="../scripts/jquery-3.4.0.min.js"%></script>
    <script><%@include file="../scripts/tasks.js"%></script>
    <style><%@include file="styles/styles.css"%></style>
</head>
<body>
<h2>Добро пожаловать, ${sessionScope.name}!</h2><br>

<h2>Все задачи</h2><button id="show-add-task-form">Добавить дело</button>

<div id="task-form">
    <form method="post" action="${pageContext.request.contextPath}/tasks/">
        <h2>Добавление задачи</h2>

        <label>Название задачи: </label>
        <input type="text" required name="task_name" value="">
        <label>Описание: </label>
        <input type="text" required name="description" value="">
        <label>Срок выполнения: </label>
        <input type="datetime-local" name="due_date" value="">
        <label>Выполнено? : </label>
        <input type="checkbox" name="is_done" value="false">
        <input hidden type="number" name="creator_id" value="${sessionScope.userId}">
        <button id="save_task"type="submit">Сохранить</button>
    </form>
</div>


<c:forEach var="task" items="${sessionScope.tasks}">
    <ul>
        Название: <c:out value="${task.taskName}"/> <br>
        Описание: <c:out value="${task.description}"/> <br>
        Срок выполнения: <c:out value="${task.dueDate}"/> <br>
        Выполнено?: <c:out value="${task.done}"/> <br>

        <form method="get" action="<c:url value='/tasks/${task.id}'/>">
            <input type="number" hidden name="id" value="${task.id}"/>
            <input type="submit" value="Редактировать"/>
        </form>
        <form method="post" action="<c:url value='/tasks/${task.id}'/>">
            <input type="number" hidden name="id" value="${task.id}"/>
            <input type="submit" name="delete" value="Удалить"/>
        </form>

    </ul>
    <hr/>

</c:forEach>

<a href="<c:url value="/logout"/>">Logout</a>
</body>
</html>
