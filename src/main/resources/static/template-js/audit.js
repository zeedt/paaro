$body = $("body");

var token = "";

$(document).bind({
    ajaxStart: function() {
        $body.addClass("loading");
    },
    ajaxStop: function() { $body.removeClass("loading"); }
});

redirectToPage();

function redirectToPage() {
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

function auditPreviousPage() {
    var pageData = $("#currentPage").html();
    var filter = $("#audit-filter").val();
    var pageNo = parseInt(pageData);

    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();
    if (pageNo < 1) {
        return;
    }
    window.location.href = "/view/audit-log?pageNo="+(pageNo-1)+"&filter="+filter+"&fromDate="+fdate+"&toDate="+tdate;
}

function filterAudit() {
    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();
    var pageData = $("#audit-filter").val();
    window.location.href = "/view/audit-log?pageNo=0&filter="+pageData+"&fromDate="+fdate+"&toDate="+tdate;
}

function auditNextPage() {
    var pageData = $("#currentPage").html();
    var filter = $("#audit-filter").val();
    var pageNo = parseInt(pageData);
    var totalNoOfPagesData = $("#totalNoOfPages").html();
    var totalNoOfPages = parseInt(totalNoOfPagesData);

    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();
    if ((pageNo+1) >= totalNoOfPages) {
        return;
    }
    window.location.href = "/view/audit-log?pageNo="+(pageNo+1)+"&filter="+filter+"&fromDate="+fdate+"&toDate="+tdate;
}

function showDetailsModal(action, ipAddress, performedBy, module, datePerformed, initialData, finalData) {

    $("#module-name").html(module);
    $("#actor-name").html(performedBy);
    $("#action-performed").html(action);
    $("#ip-address").html(ipAddress);
    $("#date-performed").html(datePerformed);
    $("#initial-data").html(initialData);
    $("#final-data").html(finalData);

    $("#audit-view").modal();

}

function downloadLog() {

    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();

    var filter = $("#audit-filter").val();
    window.location.href = "/view/audit/downloadLogs?filter="+filter+"&fromDate="+fdate+"&toDate="+tdate+"&access_token="+localStorage.getItem("paaro_access_token");

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