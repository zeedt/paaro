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


function showCurrencyModal(type, pageNo, updateContent) {

    var filter = $("#wallet-filter").val();
    if (pageNo == undefined || pageNo == null || pageNo < 0) {
        pageNo = 0;
    }

    var requestData = {
        "currencyType": type,
        "pageNo": pageNo,
        "pageSize": 10,
        "filter" : filter
    };

    $.ajax({
        type: "POST",
        beforeSend: function (request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        url: "/view/currency/findAllWalletByCurrencyPaged",
        async:false,
        data: JSON.stringify(requestData),
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
                bootbox.alert("You are not authorised to view transaction details of user");
                return;
            }
        },
        success: function (data) {
            $("#modal-currency-content").html(data);
            $("#currency-type").html(type);
            $("#wallet-filter").val(filter);
            if (updateContent == undefined || !updateContent) {
                $("#view-user").modal();
            }
            $("#wallet-filter").focus();
        }
    });

}

function walletPreviousPage() {
    var type = $("#currency-type").html();
    var pageData = $("#walletCurrentPage").html();
    var pageNo = parseInt(pageData);

    if ((pageNo-1) < 0) {
        return;
    }
    return showCurrencyModal(type,(pageNo-1), true);
}

function walletNextPage() {

    var type = $("#currency-type").html();
    var pageData = $("#walletCurrentPage").html();
    var pageNo = parseInt(pageData);
    var totalNoOfPagesData = $("#walletReqTotalNoOfPages").html();
    totalNoOfPagesData = parseInt(totalNoOfPagesData);

    if ((pageNo+1) >= totalNoOfPagesData) {
        return;
    }
    return showCurrencyModal(type,(pageNo+1), true);
}

function filterWallet() {
    var type = $("#currency-type").html();
    return showCurrencyModal(type,0)
}


function addCurrency() {

    var currencyCode = $("#currencyCode").val();
    var conversionRateToNaira = $("#conversionRateToNaira").val();
    var description = $("#description").val();

    var requestData = {
        "type" : currencyCode,
        "rateToNaira" : conversionRateToNaira,
        "decsription" : description
    };

    $.ajax({
        type : "POST",
        beforeSend: function(request) {
            request.setRequestHeader("Authorization", "Bearer " + localStorage.getItem("paaro_access_token"));
        },
        async:false,
        data: JSON.stringify(requestData),
        contentType: "application/json; charset=utf-8",
        url : "/currency",
        error : function (e) {
            console.log("status code is " + e.status);
            if (e.status == '401') {
                console.log("status code is " + 401);
                localStorage.clear();
                window.location.href = "/login";
                return;
            }

            if (e.status == '403') {
                bootbox.alert("You are not authorised to add new currency");
                return;
            }
        },
        success : function (data) {
            bootbox.alert(""+data.message+"");
        }
    });

}

function showAddCurrencyModal() {
    $("#add-currency").modal();
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