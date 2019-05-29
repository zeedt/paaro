package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apirequestmodel.BankRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankResponseModel;
import com.plethub.paaro.core.appservice.services.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("banks")
public class BankApiController {

    @Autowired
    BankService bankService;

    @GetMapping("/all")
    public BankResponseModel fetchAllBanks() {

        return bankService.fetchAllBanks();

    }

    @GetMapping("/foreign")
    public BankResponseModel fetchAllForeignBanks() {

        return bankService.fetchAllForeignBanks();

    }

    @GetMapping("/foreign/paged")
    public BankResponseModel fetchPagedForeignBanks(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {

        return bankService.fetchPagedForeignBanks(pageNo, pageSize);

    }

    @GetMapping("/local/paged")
    public BankResponseModel fetchPagedLocalBanks(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {

        return bankService.fetchPagedLocalBanks(pageNo, pageSize);

    }

    @GetMapping("/local")
    public BankResponseModel fetchAllLocalBanks() {

        return bankService.fetchAllLocalBanks();

    }

    @PostMapping("/validate-account-number")
    public BankResponseModel validateAccountNumber(@RequestBody BankRequestModel bankRequestModel) {
        return bankService.validateAccountNumber(bankRequestModel);
    }

    @PreAuthorize(value = "hasAnyAuthority('ADD_BANK')")
    @PostMapping("/add-bank")
    public BankResponseModel addBank(@RequestBody BankRequestModel bankRequestModel, HttpServletRequest servletRequest) {
        return bankService.addBank(bankRequestModel, servletRequest);
    }

    @PreAuthorize(value = "hasAnyAuthority('UPDATE_BANK')")
    @PostMapping("/update-bank")
    public BankResponseModel updateBank(@RequestBody BankRequestModel bankRequestModel, HttpServletRequest servletRequest) {
        return bankService.updateBank(bankRequestModel, servletRequest);
    }

}
