redirectToPage();

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

function updateGeneralSettings() {
    var charge = $("#chargeRate").val();
    var comment = $("#comment").val();
    var enableSms =  $("#activateSms").is(':checked');
    var enableEmail =  $("#activateEmail").is(':checked');

    var requestData = {
        "chargeRate" : charge,
        "enableSmsAlert" : enableSms,
        "enableEmailAlert" : enableEmail,
        "comment" : comment
    };

    $.ajax({
        type : "POST",
        beforeSend: function(request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url : "/settings/create-or-update",
        data: JSON.stringify(requestData),
        contentType: "application/json; charset=utf-8",
        error : function (e) {
            $("#information").css("visibility","hidden");
            console.log("status code is " + e.status);
            if (e.status == '401') {
                console.log("status code is " + 401);
                localStorage.clear();
                window.location.href = "/login";
                return;
            }

            if (e.status == '403') {
                bootbox.alert("You are not authorised to initiate transaction for user");
                return;
            }

            bootbox.alert("Unable to complete the process");
        },
        success : function (data) {
            if (data.responseStatus=="SUCCESSFUL") {
                window.location.href = "/view/setting-view";
                return;
            }
            bootbox.alert("" + data.message + "");
        }
    });
}

function verifyGeneralSettings() {
    var comment = $("#comment").val();

    var requestData = {
        "comment" : comment
    };

    $.ajax({
        type : "POST",
        beforeSend: function(request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url : "/settings/verify",
        data: JSON.stringify(requestData),
        contentType: "application/json; charset=utf-8",
        error : function (e) {
            $("#information").css("visibility","hidden");
            console.log("status code is " + e.status);
            if (e.status == '401') {
                console.log("status code is " + 401);
                localStorage.clear();
                window.location.href = "/login";
                return;
            }

            if (e.status == '403') {
                bootbox.alert("You are not authorised to verify general settings");
                return;
            }

            bootbox.alert("Unable to complete the process");
        },
        success : function (data) {
            if (data.responseStatus=="SUCCESSFUL") {
                window.location.href = "/view/setting-view";
                return;
            }
            bootbox.alert("" + data.message + "");
        }
    });
}

function declineGeneralSettings() {
    var comment = $("#comment").val();

    var requestData = {
        "comment" : comment
    };

    $.ajax({
        type : "POST",
        beforeSend: function(request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url : "/settings/decline",
        data: JSON.stringify(requestData),
        contentType: "application/json; charset=utf-8",
        error : function (e) {
            $("#information").css("visibility","hidden");
            console.log("status code is " + e.status);
            if (e.status == '401') {
                console.log("status code is " + 401);
                localStorage.clear();
                window.location.href = "/login";
                return;
            }

            if (e.status == '403') {
                bootbox.alert("You are not authorised to decline general settings");
                return;
            }

            bootbox.alert("Unable to complete the process");
        },
        success : function (data) {
            if (data.responseStatus=="SUCCESSFUL") {
                window.location.href = "/view/setting-view";
                return;
            }
            bootbox.alert("" + data.message + "");
        }
    });
}

function logoutUser() {

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