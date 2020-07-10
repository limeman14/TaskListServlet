$(function () {
    //Show adding form
    $('#show-add-task-form').click(function(){
        $('#task-form').css('display', 'flex');
    });

    //Closing adding form
    $('#task-form').click(function(event){
        if(event.target === this) {
            $(this).css('display', 'none');
        }
    });

    //Adding task
    $('#save_task').click(function()
    {
        let formdata = $("#task-form form").serializeArray();
        let data = {};
        $(formdata ).each(function(index, obj){
            data[obj.name] = obj.value;
        });
        data["is_done"] = !!$("#checkbox_check").is(':checked');

        let json = JSON.stringify(data);
        $.ajax({
            data_type: "json",
            contentType: "application/json; charset=utf-8",
            method: "POST",
            url: '/tasks/',
            data: json,
            success: function(response)
            {
                console.log("done");
                $('#task-form').css('display', 'none');
                window.location.reload(true);
            }
        });
        return false;
    });

    //Show changing form
    $('.task-change').click(function(){
        let taskId = $(this).attr('id');
        $('#task-change-form input[name="task_id"]').attr('value', taskId)
        $('#task-change-form').css('display', 'flex');
    });

    //Closing changing form
    $('#task-change-form').click(function(event){
        if(event.target === this) {
            $(this).css('display', 'none');
        }
    });

    //Changing task
    $('#save_changed_task').click(function()
    {
        let formdata = $("#task-change-form form").serializeArray();
        let data = {};
        $(formdata ).each(function(index, obj){
            data[obj.name] = obj.value;
        });
        data["is_done"] = !!$("#change_checkbox_check").is(':checked');
        let taskId = data["task_id"]
        let json = JSON.stringify(data);
        $.ajax({
            data_type: "json",
            contentType: "application/json; charset=utf-8",
            method: "PUT",
            url: '/tasks/' + taskId,
            data: json,
            success: function(response)
            {
                $('#task-change-form').css('display', 'none');
                window.location.reload(true);
            },
            error: function(response){
                alert(response)
            }
        });
        return false;
    });

    //Deleting task
    $('.task-delete').click(function(){
        let taskId = $(this).attr('id')
        $.ajax({
            method: "DELETE",
            url: '/tasks/' + taskId,
            contentType: "text/html;charset=UTF-8",
            processData: false,
            success: function(response)
            {
                alert(response);
                window.location.reload(true);
            },
            error: function(response)
            {
                if(response.status === 404) {
                    alert('Дело не найдено!');
                }
            }
        });
        return false;
    });
})


