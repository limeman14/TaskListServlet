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
    $('#save_task').submit(function(event)
    {
        event.preventDefault();
        var data = new FormData($('#task-form'))
        console.log(data)
        $.ajax({
            contentType: "application/json",
            type: "POST",
            url: '/tasks/',
            data: data,
            success: function(response)
            {
                $('#task-form').css('display', 'none');
            }
        });
        return false;
    });
})


