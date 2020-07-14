<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Текущие цели</title>
    <script type="text/javascript" charset="utf-8"><%@include file="../scripts/jquery-3.4.0.min.js"%></script>
    <script type="text/javascript" charset="utf-8"><%@include file="../scripts/goals.js"%></script>
    <style><%@include file="styles/styles.css"%></style>
</head>
<body>
<h2>Добро пожаловать, ${sessionScope.name} ${sessionScope.surname}!</h2>

<h2><a href="/tasksPage">Все задачи</a> Все цели</h2><button id="show-add-goal-form">Добавить цель</button>

<c:forEach var="goal" items="${requestScope.goals}">
    <ul>
        ID: <c:out value="${goal.id}"/><br>
        Название: <c:out value="${goal.description}"/><br>
        <c:if test="${goal.parentGoal != 0}">Цель верхнего уровня: <a href="/goals/${goal.parentGoal}">${goal.parentGoal}</a><br></c:if>
        Связанные задания:
        <c:forEach var="task" items="${goal.assignedTasks}">
        <ul>
            <li><a href="/tasks/${task.id}">${task.taskName}</a></li>
        </ul>
        </c:forEach><br>
        <a href="#" class="goal-change" id="${goal.id}"><span style="color: blue">Добавить цель верхнего уровня</span></a>&nbsp;
        <a href="#" class="goal-delete" id="${goal.id}"><span style="color: red">Удалить цель</span></a>
    </ul>
    <hr/>
</c:forEach><br>

<div id="goal-form">
    <form>
        <h2>Добавление цели</h2>

        <label>Название цели: </label>
        <input type="text" required name="description" value="">
        <input hidden type="number" name="creator_id" value="${sessionScope.userId}">
        <button id="save_goal">Сохранить</button>
    </form>
</div>

<div id="goal-change-form">
    <form>
        <h2>Добавление цели верхнего уровня</h2>

        <label>ID цели-родителя: </label>
        <input type="text" name="parent_goal_id" min="0" value="">
        <input hidden type="number" name="goal_id" value="">
        <button id="save_changed_goal">Сохранить</button>
    </form>
</div>

<a href="<c:url value="/logout"/>">Выйти из системы</a>

</body>
</html>
