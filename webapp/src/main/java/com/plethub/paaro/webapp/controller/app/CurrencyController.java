package com.plethub.paaro.webapp.controller.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plethub.paaro.core.appservice.apirequestmodel.BankRequestModel;
import com.plethub.paaro.core.appservice.apirequestmodel.CurrencyDetailsRequest;
import com.plethub.paaro.core.appservice.apirequestmodel.CurrencyRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankResponseModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.CurrencyDetailsresponse;
import com.plethub.paaro.core.appservice.apiresponsemodel.CurrencyResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.BankType;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import com.plethub.paaro.core.appservice.services.CurrencyDetailsService;
import com.plethub.paaro.core.appservice.services.CurrencyService;
import com.plethub.paaro.core.appservice.services.PaaroBankAccountService;
import com.plethub.paaro.core.faq.service.FaqService;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.models.Currency;
import com.plethub.paaro.core.models.CurrencyDetails;
import com.plethub.paaro.webapp.exception.PaaroException;
import com.plethub.paaro.webapp.service.BankService;
import com.sun.org.apache.xpath.internal.operations.Mod;
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
@RequestMapping("/app/currency")
public class CurrencyController {

    @Autowired
    CurrencyService currencyService;

    @Autowired
    PaaroBankAccountService bankAccountService;

    @Autowired
    CurrencyDetailsService currencyDetailService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @GetMapping
    public String all(Model model) {
        return "currency/view";
    }

    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("currencies", currencyService.getAllCurrencies());
        model.addAttribute("currency", new Currency());
    }

    @GetMapping("/new")
    public String newCurrency(Model model) {
        return "currency/add";
    }

    @PostMapping
    public String saveNewCurrency(@Valid Currency currency, BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "currency/add";
        }

        try {

            CurrencyRequest currencyRequest = new CurrencyRequest();
            BeanUtils.copyProperties(currency, currencyRequest);

            CurrencyResponse message = currencyService.addCurrency(currencyRequest, httpServletRequest);

            redirectAttributes.addFlashAttribute("message", message.getMessage());

            return "redirect:/app/currency";
        } catch (JsonProcessingException e) {
            result.addError(new ObjectError("error", e.getMessage()));
            model.addAttribute("failure", "error occured while creating currency");
            logger.error("Error creating content", e);
            return "currency/add";
        } catch (PaaroException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());
            logger.error("Error creating content", ibe);
            return "currency/add";
        }
    }

    @GetMapping("/{type}/enable")
    public String enableCurrency(@PathVariable String type, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            CurrencyResponse currencyResponse = currencyService.enableCurrency(type, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", currencyResponse.getMessage());
            return "redirect:/app/currency";
        } catch (Exception e) {
            model.addAttribute("failure", e.getMessage());
            return "currency/view";
        }
    }

    @GetMapping("/{type}/disable")
    public String disableCurrency(@PathVariable String type, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            CurrencyResponse currencyResponse = currencyService.disableCurrency(type, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", currencyResponse.getMessage());
            return "redirect:/app/currency";
        } catch (Exception e) {
            model.addAttribute("failure", e.getMessage());
            return "currency/view";
        }

    }

    @GetMapping("/{type}/view")
    public String currencyDetail(@PathVariable String type, Model model) {
        CurrencyResponse currencyResponse = currencyService.getCurrencyByCode(type);
        model.addAttribute("currency", currencyResponse.getCurrency());
        CurrencyDetailsresponse currencyDetailsresponse = currencyDetailService.getCurrencydetailsByCode(type);
        model.addAttribute("currencyDetail", currencyDetailsresponse.getCurrencyDetails());
        model.addAttribute("bankAccounts", bankAccountService.getPaaroBankAccountByCurrencyType(type).getPaaroBankAccounts());
        return "currency/detail";
    }

    @GetMapping("/{type}/edit")
    public String editCurrencyDetail(@PathVariable String type, Model model) {
        CurrencyResponse currencyResponse = currencyService.getCurrencyByCode(type);
        model.addAttribute("currency", currencyResponse.getCurrency());
        CurrencyDetailsresponse currencyDetailsresponse = currencyDetailService.getCurrencydetailsByCode(type);
        if (null == currencyDetailsresponse.getCurrencyDetails()) {
            model.addAttribute("currencyDetail", new CurrencyDetails());
        } else {
            model.addAttribute("currencyDetail", currencyDetailsresponse.getCurrencyDetails());
        }
        return "currency/edit";
    }

    @PostMapping("/update")
    public String updateCurrencyDetail(@Valid CurrencyDetails currencyDetails, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "currency/edit";
        }

        try {

            CurrencyDetailsRequest currencyRequest = new CurrencyDetailsRequest();
            BeanUtils.copyProperties(currencyDetails, currencyRequest);

            CurrencyDetailsresponse message = currencyDetailService.updateCurrencyDetailsForCurrency(currencyRequest, httpServletRequest);

            if (message.getApiResponseCode().equals(ApiResponseCode.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", message.getMessage());
                return "redirect:/app/currency/" + currencyDetails.getCurrencyCode() + "/view";

            } else {
                model.addAttribute("failure", message.getMessage());
                logger.error("Error creating content", message.getMessage());
                return "currency/edit";
            }
        } catch (PaaroException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            CurrencyResponse currencyResponse = currencyService.getCurrencyByCode(currencyDetails.getCurrencyCode());
            model.addAttribute("currency", currencyResponse.getCurrency());
            CurrencyDetailsresponse currencyDetailsresponse = currencyDetailService.getCurrencydetailsByCode(currencyDetails.getCurrencyCode());
            model.addAttribute("currencyDetail", currencyDetailsresponse.getCurrencyDetails());
            model.addAttribute("failure", ibe.getMessage());
            logger.error("Error updating currency details", ibe);
            return "currency/edit";
        }
    }

}
