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
        var formdata = $("#task-form form").serializeArray();
        var data = {};
        $(formdata ).each(function(index, obj){
            data[obj.name] = obj.value;
        });
        data["is_done"] = !!$("#checkbox_check").is(':checked');

        let json = JSON.stringify(data);
        console.log();
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
})


