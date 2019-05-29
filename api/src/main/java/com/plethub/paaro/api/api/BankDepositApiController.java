package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apirequestmodel.BankDepositRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankDepositResponseModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankDepositValidationResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.DepositStatus;
import com.plethub.paaro.core.appservice.repository.BankDepositRepository;
import com.plethub.paaro.core.appservice.repository.PaymentReferenceRepository;
import com.plethub.paaro.core.appservice.services.BankDepositService;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.models.BankDeposit;
import com.plethub.paaro.core.models.PaymentReference;
import com.plethub.paaro.core.models.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/bank-deposits")
public class BankDepositApiController {

    @Autowired
    private BankDepositService bankDepositService;

    private Logger logger = LoggerFactory.getLogger(BankDepositApiController.class.getName());

    @GetMapping("/get-logged-in-user-by-currency")
    public BankDepositResponseModel getAllLoggedInUserBankDepositsByCurrency(@RequestParam("currencyType") String currencyType) {

        try {

            return bankDepositService.getAllLoggedInUserBankDepositsByCurrency(currencyType);

        }  catch (Exception e) {
            logger.error("Error occurred while fetching deposit for user by currency due to ", e);
            return BankDepositResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching user's bank deposit details");
        }

    }


    @GetMapping("/get-all-for-logged-in-user")
    public BankDepositResponseModel getAllLoggedInUserBankDeposits(@RequestParam(value = "pageNo", required = false) Integer pageNo, @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        try {

            return bankDepositService.getAllLoggedInUserBankDeposits(pageNo, pageSize);

        }  catch (Exception e) {
            logger.error("Error occurred while fetching all deposits for user due to ", e);
            return BankDepositResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching user's bank deposit details");
        }

    }


    @GetMapping("/get-all-pending-verification-for-logged-in-user")
    public BankDepositResponseModel getAllLoggedInUserPendingBankDeposits(@RequestParam(value = "pageNo", required = false) Integer pageNo, @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        try {

            return bankDepositService.getAllLoggedInUserPendingBankDeposits(pageNo, pageSize);

        }  catch (Exception e) {
            logger.error("Error occurred while fetching all pending deposit due to ", e);
            return BankDepositResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching user's bank deposit details");
        }

    }


    @PostMapping("/create-for-logged-in-user")
    public BankDepositResponseModel logBankDepositForUser(@RequestBody BankDepositRequestModel bankDepositRequestModel, HttpServletRequest servletRequest) {

        try {

            return bankDepositService.logBankDepositForUser(bankDepositRequestModel, servletRequest);

        }  catch (Exception e) {
            logger.error("Error occurred while logging deposit due to ", e);
            return BankDepositResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while logging deposit details");
        }

    }

    @PreAuthorize("hasAuthority('APPROVE_DEPOSIT')")
    @GetMapping("/approve-deposit")
    public BankDepositResponseModel approveBankDepositForUser(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {

            return bankDepositService.approveBankDepositForUser(id, servletRequest);

        }  catch (Exception e) {
            logger.error("Error occurred while approving deposit due to ", e);
            return BankDepositResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while approving bank deposit");
        }

    }

    @PostMapping("/fetch-deposit-with-and-param")
    public BankDepositResponseModel searchForBankDepositModelWithAnd(@RequestBody BankDepositRequestModel requestModel) {

        try {

            return bankDepositService.searchForBankDepositModelWithAnd(requestModel);

        }  catch (Exception e) {
            logger.error("Error occurred while etching bank deposit requests with and due to ", e);
            return BankDepositResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching bank deposit requests with and params");
        }

    }
    @PostMapping("/fetch-deposit-with-or-param")
    public BankDepositResponseModel searchForBankDepositModelWithOr(@RequestBody BankDepositRequestModel requestModel) {

        try {

            return bankDepositService.searchForBankDepositModelWithOr(requestModel);

        }  catch (Exception e) {
            logger.error("Error occurred while etching bank deposit requests with or due to ", e);
            return BankDepositResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching bank deposit requests with or params");
        }

    }



}
