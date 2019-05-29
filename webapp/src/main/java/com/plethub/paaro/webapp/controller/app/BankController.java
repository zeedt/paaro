package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.appservice.apirequestmodel.BankRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.BankType;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.webapp.ApiModel.UserDetails;
import com.plethub.paaro.webapp.exception.PaaroException;
import com.plethub.paaro.webapp.service.AuthService;
import com.plethub.paaro.webapp.service.BankService;
import com.plethub.paaro.webapp.util.Paged;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;

@Controller
@RequestMapping("/app/bank")
public class BankController {

    @Autowired
    BankService bankService;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    com.plethub.paaro.core.appservice.services.BankService coreBankService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @GetMapping("/all")
    public String all(Model model){
        return "bank/all";
    }

    @GetMapping(path = "/all/all")
    public
    @ResponseBody
    DataTablesOutput<Bank> getAllBanks(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<Bank> sq = bankService.getAllBanks(pageable);
        DataTablesOutput<Bank> out = new DataTablesOutput<Bank>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping("/local")
    public String local(Model model){
        return "bank/local";
    }

    @GetMapping(path = "/local/all")
    public
    @ResponseBody
    DataTablesOutput<Bank> getLocalBanks(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<Bank> sq = bankService.getLocalBanks(pageable);
        DataTablesOutput<Bank> out = new DataTablesOutput<Bank>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping("/foreign")
    public String foreign(Model model){
        return "bank/foreign";
    }

    @GetMapping(path = "/foreign/all")
    public
    @ResponseBody
    DataTablesOutput<Bank> getForeignBanks(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<Bank> sq = bankService.getForeignBanks(pageable);
        DataTablesOutput<Bank> out = new DataTablesOutput<Bank>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("types", BankType.values());
    }

    @GetMapping("/new")
    public String newBank(Model model){
        model.addAttribute("bank",new Bank());
        return "bank/add";
    }

    @PostMapping
    public String createBank(@Valid Bank bank, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale){
        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "bank/add";
        }

        try {

            BankRequestModel bankRequestModel = new BankRequestModel();
            BeanUtils.copyProperties(bank,bankRequestModel);
            BankResponseModel message = coreBankService.addBank(bankRequestModel, httpServletRequest);

            if (message.getResponseCode().equals(ApiResponseCode.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", message.getMessage());
                if (bank.getBankType().equals(BankType.FOREIGN)){
                    return "redirect:/app/bank/foreign";
                }else {
                    return "redirect:/app/bank/local";
                }
            }else {
                model.addAttribute("failure", message.getMessage());
                logger.error("Error creating content",message.getMessage());
                return "bank/add";
            }
        }catch (PaaroException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());

            logger.error("Error creating content",ibe);
            return "bank/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editBank(@PathVariable Long id, Model model){
        model.addAttribute("bank", bankService.getBankDetails(id));
        return "bank/edit";
    }

    @PostMapping("/update")
    public String updateBank(@Valid Bank bank, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale){
        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "bank/edit";
        }

        try {

            BankRequestModel bankRequestModel = new BankRequestModel();
            BeanUtils.copyProperties(bank,bankRequestModel);
            BankResponseModel message = coreBankService.updateBank(bankRequestModel, httpServletRequest);
            if (message.getResponseCode().equals(ApiResponseCode.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", message.getMessage());
                if (bank.getBankType().equals(BankType.FOREIGN)){
                    return "redirect:/app/bank/foreign";
                }else {
                    return "redirect:/app/bank/local";
                }
            }else {
                model.addAttribute("failure", message.getMessage());
                logger.error("Error creating content",message.getMessage());
                return "bank/edit";
            }

        }catch (PaaroException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());

            logger.error("Error creating content",ibe);
            return "bank/edit";
        }
    }
}
