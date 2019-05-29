
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


function loadTransferRequestTransactions(pageNo, filter) {


    if (pageNo == 'null' || pageNo == null || pageNo == undefined || pageNo == "" || pageNo == "undefined") {
        pageNo = 0;
    }

    if (filter == 'null' || filter == null || filter == undefined || filter == "" || filter == "undefined") {
        filter = "";
    }

    if (pageNo == undefined || pageNo == null || pageNo < 0) {
        pageNo = 0;
    }

    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();
    showLoader = true;

    $.ajax({
        type: "GET",
        beforeSend: function (request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url: "/view/dashboard/findAllWalletTransferTransactionsPaged?pageNo="+pageNo+"&filter="+filter+"&fromDate="+fdate+"&toDate="+tdate,
        async:false,
        // data: JSON.stringify(requestData),
        contentType: "application/json; charset=utf-8",
        error: function (e) {
            console.log("status code is " + e.status);
            if (e.status == '401') {
                console.log("status code is " + 401);
                localStorage.clear();
                window.location.href = "/login";
                return;
            }

            if (e.status == '403') {
                bootbox.alert("You are not authorised to view transaction details");
                return;
            }
        },
        success: function (data) {
            $("#dashboard-content").html(data);
            console.log("data generated");
            $("#transfer-filter").val(filter);
            $("#fromDate").val(fdate);
            $("#toDate").val(tdate);
            // $("#view-user").modal();
            $("#transfer-filter").focus();
        }
    });

}

function loadFundingRequestTransactions(pageNo, filter) {

    if (pageNo == 'null' || pageNo == null || pageNo == undefined || pageNo == "" || pageNo == "undefined") {
        pageNo = 0;
    }

    if (filter == 'null' || filter == null || filter == undefined || filter == "" || filter == "undefined") {
        filter = "";
    }
    if (pageNo == undefined || pageNo == null || pageNo < 0) {
        pageNo = 0;
    }

    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();
    showLoader = true;

    $.ajax({
        type: "GET",
        beforeSend: function (request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url: "/view/dashboard/findAllWalletFundingTransactionsPaged?pageNo="+pageNo+"&filter="+filter+"&fromDate="+fdate+"&toDate="+tdate,
        async:false,
        // data: JSON.stringify(requestData),
        contentType: "application/json; charset=utf-8",
        error: function (e) {
            console.log("status code is " + e.status);
            if (e.status == '401') {
                console.log("status code is " + 401);
                localStorage.clear();
                window.location.href = "/login";
                return;
            }

            if (e.status == '403') {
                bootbox.alert("You are not authorised to view transaction details");
                return;
            }
        },
        success: function (data) {
            $("#dashboard-content").html(data);
            console.log("data generated");
            $("#transfer-filter").val(filter);
            // $("#view-user").modal();
            $("#transfer-filter").focus();
        }
    });

}

function loadTransferRequestTransactionsByButton(pageNo, filter) {

    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();

    var filter = $("#transfer-filter").val();
    if (pageNo == undefined || pageNo == null || pageNo < 0) {
        pageNo = 0;
    }
    showLoader = true;

    $.ajax({
        type: "GET",
        beforeSend: function (request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url: "/view/dashboard/findAllWalletTransferTransactionsPaged?pageNo="+pageNo+"&filter="+filter+"&fromDate="+fdate+"&toDate="+tdate,
        async:false,
        // data: JSON.stringify(requestData),
        contentType: "application/json; charset=utf-8",
        error: function (e) {
            console.log("status code is " + e.status);
            if (e.status == '401') {
                console.log("status code is " + 401);
                localStorage.clear();
                window.location.href = "/login";
                return;
            }

            if (e.status == '403') {
                bootbox.alert("You are not authorised to view transaction details");
                return;
            }
        },
        success: function (data) {
            $("#dashboard-content").html(data);
            console.log("data generated");
            $("#transfer-filter").val(filter);
            $("#fromDate").val(fdate);
            $("#toDate").val(tdate);
            // $("#view-user").modal();
            $("#transfer-filter").focus();
        }
    });

}

function loadFundingRequestTransactionsByButton(pageNo, filter) {

    var filter = $("#transfer-filter").val();
    if (pageNo == undefined || pageNo == null || pageNo < 0) {
        pageNo = 0;
    }


    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();
    showLoader = true;

    $.ajax({
        type: "GET",
        beforeSend: function (request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url: "/view/dashboard/findAllWalletFundingTransactionsPaged?pageNo="+pageNo+"&filter="+filter+"&fromDate="+fdate+"&toDate="+tdate,
        async:false,
        // data: JSON.stringify(requestData),
        contentType: "application/json; charset=utf-8",
        error: function (e) {
            console.log("status code is " + e.status);
            if (e.status == '401') {
                console.log("status code is " + 401);
                localStorage.clear();
                window.location.href = "/login";
                return;
            }

            if (e.status == '403') {
                bootbox.alert("You are not authorised to view transaction details");
                return;
            }
        },
        success: function (data) {
            $("#dashboard-content").html(data);
            console.log("data generated");
            $("#transfer-filter").val(filter);
            // $("#view-user").modal();
            $("#transfer-filter").focus();
        }
    });

}

function loadNextPage() {

    var pageData = $("#transferReqCurrentPage").html();
    var filter = $("#transfer-filter").val();
    var pageNo = parseInt(pageData);
    var totalNoOfPagesData = $("#transferReqTotalNoOfPages").html();
    var totalNoOfPages = parseInt(totalNoOfPagesData);
    if ((pageNo+1) >= totalNoOfPages) {
        return;
    }

    loadTransferRequestTransactionsByButton(parseInt(pageNo+1), filter);

}

function loadPreviousPage() {

    var pageData = $("#transferReqCurrentPage").html();
    var filter = $("#transfer-filter").val();
    var pageNo = parseInt(pageData);
    if (pageNo < 1) {
        return;
    }

    loadTransferRequestTransactionsByButton(parseInt(pageNo-1), filter);

}
function loadFundingNextPage() {

    var pageData = $("#transferReqCurrentPage").html();
    var filter = $("#transfer-filter").val();
    var pageNo = parseInt(pageData);
    var totalNoOfPagesData = $("#transferReqTotalNoOfPages").html();
    var totalNoOfPages = parseInt(totalNoOfPagesData);
    if ((pageNo+1) >= totalNoOfPages) {
        return;
    }

    loadFundingRequestTransactionsByButton(parseInt(pageNo+1), filter);

}

function loadFundingPreviousPage() {

    var pageData = $("#transferReqCurrentPage").html();
    var filter = $("#transfer-filter").val();
    var pageNo = parseInt(pageData);
    if (pageNo < 1) {
        return;
    }

    loadFundingRequestTransactionsByButton(parseInt(pageNo-1), filter);

}

function filterTransferRequestTransaction() {
    var filter = $("#transfer-filter").val();
    $("#transferReqCurrentPage").html("0");

    return loadTransferRequestTransactions(0,filter);
}

function filterFundingRequestTransaction() {
    var filter = $("#transfer-filter").val();
    $("#transferReqCurrentPage").html("0");

    return loadFundingRequestTransactions(0,filter);
}

function downloadTransferRequestTransaction() {

    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();

    var filter = $("#transfer-filter").val();
    window.location.href = "/view/dashboard/downloadWalletTransferTransactions?filter="+filter+"&fromDate="+fdate+"&toDate="+tdate+"&access_token="+localStorage.getItem("paaro_access_token");

}

