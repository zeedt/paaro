var showLoader = false;

redirectToPage();

$(document).bind({
    ajaxStart: function() {
        if (showLoader) {
            $body.addClass("loading");
        }
    },
    ajaxStop: function() { $body.removeClass("loading"); }
});


function redirectToPage() {
    showLoader = false;
    $.ajax({
        type : "GET",
        beforeSend: function(request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url : "/user/authority",
        error : function (e) {
            console.log("status code is " + e.status);
            if (e.status == '401') {
                console.log("status code is " + 401);
                localStorage.clear();
                window.location.href = "/login";
            }
        }
    })
}

function logoutUser() {
    showLoader = true;
    $.ajax({
        type : "GET",
        beforeSend: function(request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url : "/view/logout",
        error : function (e) {
            localStorage.clear();
            window.location.href = "/login";
        },
        success : function (data) {
            localStorage.clear();
            window.location.href = "/login";
        }
    });

}