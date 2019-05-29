package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apirequestmodel.WalletRequest;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletTransferRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.DashboardResponseModel;
import com.plethub.paaro.core.appservice.services.DashBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer-dashboard")
public class DashBoardController {

    @Autowired
    private DashBoardService dashBoardService;

    @GetMapping("/fetch-details")
    private DashboardResponseModel fetchDashboardDetails() {
        return dashBoardService.fetchDashboardDetails();
    }


    @PostMapping("/fetch-ongoing")
    private DashboardResponseModel fetchOnGoingTransactionDetailsByPage(@RequestBody  WalletRequest walletRequest) {
        return dashBoardService.fetchOngoingTransactionDetailsByPage(walletRequest);
    }

    @PostMapping("/fetch-completed")
    private DashboardResponseModel fetchCompletedTransactionDetailsByPage(@RequestBody WalletRequest walletRequest) {
        return dashBoardService.fetchCompletedTransactionDetailsByPage(walletRequest);
    }

}
