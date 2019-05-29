
var paaro_host = "http://127.0.0.1:8011/";
var user_host = "http://127.0.0.1:8011/";

$body = $("body");

$(document).bind({
    ajaxStart: function() { $body.addClass("loading");    },
    ajaxStop: function() { $body.removeClass("loading"); }
});

// var paaro_host = "http://159.65.53.209:7071/";
// var user_host = "http://159.65.53.209:8011/";

function login() {
    $("#error-message").html("");
    var email = $("#email").val();
    var password = $("#password").val();
    var base64String = Base64.encode("paaro-service:secret");

    $.ajax({
        type: "POST",
        beforeSend: function(request) {
            request.setRequestHeader("Authorization", "Basic " + base64String);
        },
        url:paaro_host + "/app/user/login",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify({email:email,password:password}),
        async:false,
        success : function (result) {
            localStorage.clear();

            if (result.message != null && result.message != 'null' && result.message != undefined && result.message != "") {
                $("#error-message").html("<h3>"+result.message+"</h3>");
                return;
            }

            localStorage.setItem("paaro_access_token", result.access_token);
            localStorage.setItem("email", result.email);
            localStorage.setItem("firstName", result.firstName);
            localStorage.setItem("lastName", result.lastName);
            localStorage.setItem("role", result.category);
            window.location.href = "/";
        },
        error : function (error) {
            $("#error-message").html("<h3>An internal server error occurred. Please contact admin if this persist</h3>");
        }
    })


}