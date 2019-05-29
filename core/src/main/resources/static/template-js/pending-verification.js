
function loadPendingRequestTransactions(pageNo, filter) {


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

    $.ajax({
        type: "GET",
        beforeSend: function (request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url: "/view/dashboard/getAllPendingRequestTransactions?pageNo="+pageNo+"&filter="+filter+"&fromDate="+fdate+"&toDate="+tdate,
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


function loadPendingRequestTransactionsByButton(pageNo, filter) {

    var fdate = $("#fromDate").val();
    var tdate = $("#toDate").val();

    var filter = $("#transfer-filter").val();
    if (pageNo == undefined || pageNo == null || pageNo < 0) {
        pageNo = 0;
    }

    $.ajax({
        type: "GET",
        beforeSend: function (request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url: "/view/dashboard/getAllPendingRequestTransactions?pageNo="+pageNo+"&filter="+filter+"&fromDate="+fdate+"&toDate="+tdate,
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

function loadPendingRequestsNextPage() {

    var pageData = $("#transferReqCurrentPage").html();
    var filter = $("#transfer-filter").val();
    var pageNo = parseInt(pageData);
    var totalNoOfPagesData = $("#transferReqTotalNoOfPages").html();
    var totalNoOfPages = parseInt(totalNoOfPagesData);
    if ((pageNo+1) >= totalNoOfPages) {
        return;
    }

    loadPendingRequestTransactionsByButton(parseInt(pageNo+1), filter);

}

function loadPendingRequestsPreviousPage() {

    var pageData = $("#transferReqCurrentPage").html();
    var filter = $("#transfer-filter").val();
    var pageNo = parseInt(pageData);
    if (pageNo < 1) {
        return;
    }

    loadPendingRequestTransactionsByButton(parseInt(pageNo-1), filter);

}

function filterPendingRequestTransaction() {
    var filter = $("#transfer-filter").val();
    $("#transferReqCurrentPage").html("0");

    return loadPendingRequestTransactionsByButton(0,filter);
}