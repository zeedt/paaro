package com.plethub.paaro.api.api;


import com.plethub.paaro.core.appservice.apirequestmodel.PaaroBankAccountRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.PaaroBankResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.PaaroBankAccount;
import com.plethub.paaro.core.appservice.services.PaaroBankAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/paaro-bank-details")
public class PaaroBankAccountController {

    @Autowired
    private PaaroBankAccountService paaroBankAccountService;

    private Logger logger = LoggerFactory.getLogger(PaaroBankAccountController.class.getName());

    @GetMapping("/get-active")
    public PaaroBankResponseModel getActivePaaroBankAccount() {

        try {
            return paaroBankAccountService.getActivePaaroBankAccount();
        } catch (Exception e) {
            logger.error("Error occurred while fetching bank account due to ", e);
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching active bank account");
        }

    }

    @GetMapping("/get-active-by-currency")
    public PaaroBankResponseModel getActivePaaroBankAccountByCurrencyType(@RequestParam("currencyType") String currencyType) {

        try {
            return paaroBankAccountService.getActivePaaroBankAccountByCurrencyType(currencyType);
        } catch (Exception e) {
            logger.error("Error occurred while fetching bank account with currency due to ", e);
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching active bank account with currency");
        }

    }

    @PreAuthorize(value = "hasAnyAuthority('ADD_PAARO_BANK_ACCOUNT')")
    @PostMapping("/add-bank-account")
    public PaaroBankResponseModel createPaaroBankAccount(@RequestBody PaaroBankAccountRequest paaroBankAccountRequest, HttpServletRequest servletRequest) {

        try {
            return paaroBankAccountService.createPaaroBankAccount(paaroBankAccountRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while creating bank account due to ", e);
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while creating bank account");
        }

    }

    @PreAuthorize(value = "hasAnyAuthority('EDIT_PAARO_BANK_ACCOUNT')")
    @PostMapping("/update-bank-account")
    public PaaroBankResponseModel updatePaaroBankAccount(@RequestBody PaaroBankAccountRequest paaroBankAccountRequest, HttpServletRequest servletRequest) {

        try {
            return paaroBankAccountService.updatePaaroBankAccount(paaroBankAccountRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while modifying bank account due to ", e);
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while modifying bank account");
        }

    }


    @GetMapping("/get-all")
    public PaaroBankResponseModel getAllPaaroBankAccount() {

        try {
            return paaroBankAccountService.getAllPaaroBankAccount();
        } catch (Exception e) {
            logger.error("Error occurred while fetching bank account due to ", e);
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching all paaro bank account");
        }

    }

    @GetMapping("/by-id")
    public PaaroBankResponseModel getPaaroBankAccountById(@RequestParam("id") Long id) {

        try {
            return paaroBankAccountService.getPaaroBankAccountById(id);
        } catch (Exception e) {
            logger.error("Error occurred while fetching bank account due to ", e);
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching paaro bank account");
        }

    }



}
