package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.appservice.apirequestmodel.BankRequestModel;
import com.plethub.paaro.core.appservice.apirequestmodel.PaaroBankAccountRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankResponseModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.PaaroBankResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.BankType;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import com.plethub.paaro.core.appservice.services.CurrencyService;
import com.plethub.paaro.core.appservice.services.PaaroBankAccountService;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.models.PaaroBankAccount;
import com.plethub.paaro.webapp.exception.PaaroException;
import com.plethub.paaro.webapp.service.BankService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;


@Controller
@RequestMapping("/app/account")
public class BankAccountController {

    @Autowired
    PaaroBankAccountService bankAccountService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    BankRepository bankRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("bankAccounts", bankAccountService.getAllPaaroBankAccount().getPaaroBankAccounts());
        model.addAttribute("currencies", currencyService.getAllCurrencies());
        model.addAttribute("banks", bankRepository.findAll());
    }

    @GetMapping
    public String view(Model model) {
        return "account/view";
    }

    @GetMapping("/new")
    public String newBank(Model model) {
        model.addAttribute("account", new PaaroBankAccountRequest());
        return "account/add";
    }

    @PostMapping
    public String createBankAccount(@Valid PaaroBankAccountRequest bankAccount, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale) {

        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "account/add";
        }

        try {
            PaaroBankResponseModel message = bankAccountService.createPaaroBankAccount(bankAccount, httpServletRequest);
            if (message.getApiResponseCode().equals(ApiResponseCode.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", message.getMessage());
                return "redirect:/app/account";
            } else {
                model.addAttribute("account", bankAccount);
                model.addAttribute("failure", message.getMessage());
                logger.error("Error creating content", message.getMessage());
                return "account/add";
            }

        } catch (PaaroException ibe) {
            model.addAttribute("account", bankAccount);
            result.addError(new ObjectError("error", ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());
            logger.error("Error creating content", ibe);
            return "account/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editBank(@PathVariable Long id, Model model) {
        PaaroBankAccount paaroBankAccount = bankAccountService.getPaaroBankAccountById(id).getPaaroBankAccount();
        PaaroBankAccountRequest paaroBankAccountRequest = new PaaroBankAccountRequest();
        BeanUtils.copyProperties(paaroBankAccount, paaroBankAccountRequest);
        paaroBankAccountRequest.setCurrencyType(paaroBankAccount.getCurrency().getType());
        model.addAttribute("account", paaroBankAccountRequest);
        return "account/edit";
    }

    @PostMapping("/update")
    public String updateBankAccount(@Valid PaaroBankAccountRequest paaroBankAccountRequest, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "account/edit";
        }

        try {
            PaaroBankResponseModel message = bankAccountService.updatePaaroBankAccount(paaroBankAccountRequest, httpServletRequest);
            if (message.getApiResponseCode().equals(ApiResponseCode.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", message.getMessage());
                return "redirect:/app/account";
            } else {
                model.addAttribute("account", paaroBankAccountRequest);
                model.addAttribute("failure", message.getMessage());
                logger.error("Error updating account", message.getMessage());
                return "account/edit";
            }

        } catch (PaaroException ibe) {
            model.addAttribute("account", paaroBankAccountRequest);
            result.addError(new ObjectError("error", ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());
            logger.error("Error creating content", ibe.getMessage());
            return "account/edit";
        }
    }

}
