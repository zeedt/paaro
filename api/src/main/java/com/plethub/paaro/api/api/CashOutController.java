package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apirequestmodel.CashOutRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.CashOutResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.services.CashOutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/cash-out")
public class CashOutController {

    @Autowired
    private CashOutService cashOutService;

    private Logger logger = LoggerFactory.getLogger(CashOutController.class.getName());

    @PostMapping("/log")
    public CashOutResponse logCashOut(@RequestBody CashOutRequest cashOutRequest, HttpServletRequest servletRequest) {
        try {
            return cashOutService.logCashOutForCustomer(cashOutRequest, servletRequest);
        } catch (Exception e) {
            logger.error("System error occurred while logging cashout due to ", e);
            return CashOutResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred");
        }
    }

    @PostMapping("/search-with-or")
    public CashOutResponse searchCashOutWithOr(@RequestBody CashOutRequest cashOutRequest) {
        try {
            return cashOutService.searchCashOutWithOr(cashOutRequest);
        } catch (Exception e) {
            logger.error("System error occurred while seraching cashout with or due to ", e);
            return CashOutResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred");
        }
    }

    @GetMapping("/search-by-page")
    public CashOutResponse searchCashOutByPage(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize ) {
        try {
            return cashOutService.searchCashOutByPage(pageNo, pageSize);
        } catch (Exception e) {
            logger.error("System error occurred while seraching cashout by page due to ", e);
            return CashOutResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred");
        }
    }

    @PostMapping("/search-with-and")
    public CashOutResponse searchCashOutWithAnd(@RequestBody CashOutRequest cashOutRequest) {
        try {
            return cashOutService.searchCashOutWithAnd(cashOutRequest);
        } catch (Exception e) {
            logger.error("System error occurred while seraching cashout with and due to ", e);
            return CashOutResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred");
        }
    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_CASHOUT')")
    @PostMapping("/approve-cashout")
    public CashOutResponse approveCashOutForCustomer(@RequestBody CashOutRequest cashOutRequest, HttpServletRequest servletRequest) {
        try {
            return cashOutService.approveCashOutForCustomer(cashOutRequest, servletRequest);

        } catch (Exception e) {
            return CashOutResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while approving cashout");
        }
    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_CASHOUT')")
    @PostMapping("/decline-cashout")
    public CashOutResponse declineCashOutForCustomer(@RequestBody CashOutRequest cashOutRequest, HttpServletRequest servletRequest) {
        try {
            return cashOutService.declineCashOutForCustomer(cashOutRequest, servletRequest);

        } catch (Exception e) {
            return CashOutResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while declining cashout");
        }
    }

}
