$body = $("body");
var showLoader = false;

$(document).bind({
    ajaxStart: function() {
        if (showLoader) {
            $body.addClass("loading");
        }
        },
    ajaxStop: function() { $body.removeClass("loading"); }
});

redirectToPage();

$(document).ready( function () {
    showLoader = true;
    var table = $('.dataTables-example').DataTable({
        "ajax" : {
            type : "GET",
            beforeSend: function(request) {
                request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
            },
            url : "/app/authority/fetchAllAuthorities/list",
            error : function (e) {
                if (e.status == '401') {
                    localStorage.clear();
                    window.location.href= "/login";
                }
            }
        },
        "sAjaxDataProp": "",
        "order": [[ 0, "asc" ]],
        columnDefs: [
            {
                "targets": 0,
                "data" : "id",
                "visible" : false
            },
            {
                "targets": 1,
                "data": "authority",

            },

            {
                "targets": 2,
                "data": "description"
            },
        ]
    });
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



function editAuthority(data) {
    showLoader = true;
    console.log("Id is " + data);
    $("#authority").html(data.authority);
    $('input[name="description"]').val(data.description);
    $("#myModal").modal();

}

function editAuthority(data) {
    $("#new-authority").modal();

}

function updateAuthority() {
    var authority =
    console.log("Updating authority");
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