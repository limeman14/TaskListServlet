$(function () {
    //Show adding form
    $('#show-add-goal-form').click(function(){
        $('#goal-form').css('display', 'flex');
    });

    //Closing adding form
    $('#goal-form').click(function(event){
        if(event.target === this) {
            $(this).css('display', 'none');
        }
    });

    //Adding goal
    $('#save_goal').click(function()
    {
        let formdata = $("#goal-form form").serializeArray();
        let data = {};
        $(formdata ).each(function(index, obj){
            data[obj.name] = obj.value;
        });
        let json = JSON.stringify(data);
        $.ajax({
            data_type: "json",
            contentType: "application/json; charset=utf-8",
            method: "POST",
            url: '/goals/',
            data: json,
            success: function(response)
            {
                $('#goal-form').css('display', 'none');
                window.location.reload(true);
            }
        });
        return false;
    });

    //Show changing form
    $('.goal-change').click(function(){
        let goalId = $(this).attr('id');
        $('#goal-change-form input[name="goal_id"]').attr('value', goalId);
        $('#goal-change-form').css('display', 'flex');
    });

    //Closing changing form
    $('#goal-change-form').click(function(event){
        if(event.target === this) {
            $(this).css('display', 'none');
        }
    });

    //Changing goal
    $('#save_changed_goal').click(function()
    {
        let formdata = $("#goal-change-form form").serializeArray();
        let data = {};
        $(formdata).each(function(index, obj){
            data[obj.name] = obj.value;
        });
        let goalId = data["goal_id"];
        let json = JSON.stringify(data);
        $.ajax({
            data_type: "json",
            contentType: "application/json; charset=utf-8",
            method: "put",
            url: '/goals/' + goalId,
            data: json,
            success: function(response)
            {
                $('#goal-change-form').css('display', 'none');
                window.location.reload(true);
            },
            error: function(response){
                alert(response)
            }
        });
        return false;
    });

    //Deleting goal
    $('.goal-delete').click(function(){
        let goalId = $(this).attr('id')
        $.ajax({
            method: "DELETE",
            url: '/goals/' + goalId,
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
                    alert('Цель не найдена!');
                }
            }
        });
        return false;
    });
})


