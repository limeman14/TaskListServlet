<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.time.format.DateTimeFormatter" %>
<html>
<head>
    <title>Текущие задачи</title>
    <script type="text/javascript" charset="utf-8"><%@include file="../scripts/jquery-3.4.0.min.js"%></script>
    <script type="text/javascript" charset="utf-8"><%@include file="../scripts/tasks.js"%></script>
    <style><%@include file="styles/styles.css"%></style>
</head>
<body>
<h2>Добро пожаловать, ${sessionScope.name} ${sessionScope.surname}!</h2>

<h2>Все задачи <a href="/goalsPage"> Все цели</a></h2><button id="show-add-task-form">Добавить дело</button>

<c:forEach var="task" items="${requestScope.tasks}">
    <ul>
        Название: <c:out value="${task.taskName}"/><br>
        Описание: <c:out value="${task.description}"/> <br>
        Срок выполнения: ${task.dueDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))}<br>
        Выполнено?: <c:out value="${task.isDone}"/> <br>
        <a href="#" class="task-change" id="${task.id}"><span style="color: blue">Изменить</span></a>&nbsp;
        <a href="#" class="task-delete" id="${task.id}"><span style="color: red">Удалить</span></a>
    </ul>
    <hr/>
</c:forEach>

<div id="task-form">
    <form>
        <h2>Добавление задачи</h2>

        <label>Название задачи: </label>
        <input type="text" required name="task_name" value="">
        <label>Описание: </label>
        <input type="text" required name="description" value="">
        <label>Срок выполнения: </label>
        <input type="datetime-local" name="due_date" value="">
        <label>Выполнено? : </label>
        <input type="checkbox" id="checkbox_check" name="is_done" value="false">
        <input hidden type="number" name="creator_id" value="${sessionScope.userId}">
        <button id="save_task">Сохранить</button>
    </form>
</div>

<div id="task-change-form">
    <form>
        <h2>Редактирование задачи</h2>

        <label>ID цели: </label>
        <input type="number" name="goal_id" min="0" value="">
        <label>Название задачи: </label>
        <input type="text" required name="task_name" value="">
        <label>Описание: </label>
        <input type="text" required name="description" value="">
        <label>Срок выполнения: </label>
        <input type="datetime-local" name="due_date" value="">
        <label>Выполнено? : </label>
        <input type="checkbox" id="change_checkbox_check" name="is_done" value="false">
        <input hidden type="number" name="creator_id" value="${sessionScope.userId}">
        <input hidden type="number" name="task_id" value="">
        <button id="save_changed_task">Сохранить</button>
    </form>
</div>

<a href="<c:url value="/logout"/>">Выйти из системы</a>
</body>
</html>
