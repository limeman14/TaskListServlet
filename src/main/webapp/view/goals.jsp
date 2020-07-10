<%@ page import="org.gurenko.vladislav.tasklistwebservice.repository.GoalRepo" %>
<%@ page import="org.gurenko.vladislav.tasklistwebservice.repository.TaskRepo" %>
<%@ page import="org.gurenko.vladislav.tasklistwebservice.model.Task" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Текущие цели</title>
</head>
<body>
<h2>Добро пожаловать, ${sessionScope.name} ${sessionScope.surname}!</h2>

<h2><a href="/tasksPage">Все задачи</a> Все цели</h2><button id="show-add-goal-form">Добавить цель</button>

<c:forEach var="goal" items="${requestScope.goals}">
    <ul>
        ID: <c:out value="${goal.id}"/><br>
        Название: <c:out value="${goal.description}"/><br>
        <c:if test="${goal.parentGoal != 0}">Надцель: <a href="/goals/${goal.parentGoal}">${goal.parentGoal}</a><br></c:if>
        Связанные задания:
        <c:forEach var="task" items="${goal.assignedTasks}">
        <ul>
            <li><a href="/tasks/${task.id}">${task.taskName}</a></li>
        </ul>
        </c:forEach><br>
        <a href="#" class="goal-change" id="${goal.id}"><span style="color: blue">Изменить цель</span></a>&nbsp;
        <a href="#" class="goal-delete" id="${goal.id}"><span style="color: red">Удалить цель</span></a>
    </ul>
    <hr/>
</c:forEach>
<a href="<c:url value="/logout"/>">Выйти из системы</a>

</body>
</html>
