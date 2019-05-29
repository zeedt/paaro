package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.appservice.apirequestmodel.BankRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.BankType;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import com.plethub.paaro.core.faq.apimodel.FaqRequest;
import com.plethub.paaro.core.faq.apimodel.FaqResponse;
import com.plethub.paaro.core.faq.service.FaqService;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.models.Faq;
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
import java.util.Date;
import java.util.Locale;


@Controller
@RequestMapping("/app/faq")
public class FaqWebController {

    @Autowired
    FaqService faqService;

    @Autowired
    BankRepository bankRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void init(Model model) {
        model.addAttribute("faqs", faqService.getFaqs());
    }

    @GetMapping
    public String all(Model model){
        return "faq/view";
    }

    @GetMapping("/new")
    public String newBank(Model model){
        model.addAttribute("faq",new Faq());
        return "faq/add";
    }

    @PostMapping
    public String createBank(@Valid Faq faq, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale){
        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "faq/add";
        }

        try {

            faq.setUpdatedDate(new Date());
            FaqRequest faqRequest = new FaqRequest();
            BeanUtils.copyProperties(faq,faqRequest);
            FaqResponse faqResponse = faqService.addFaq(faqRequest, httpServletRequest);
            if (faqResponse.getResponseCode().equals(ApiResponseCode.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", faqResponse.getMessage());
                return "redirect:/app/faq";
            } else {
                model.addAttribute("faq",faq);
                model.addAttribute("failure", faqResponse.getMessage());
                logger.error("Error creating content", faqResponse.getMessage());
                return "faq/add";
            }
        }catch (PaaroException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());

            logger.error("Error creating content",ibe);
            return "faq/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editFaq(@PathVariable Long id, Model model){
        model.addAttribute("faq", faqService.getFaq(id).getFaq());
        return "faq/edit";
    }

    @PostMapping("/update")
    public String updateBank(@Valid Faq faq, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale){

        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "faq/edit";
        }

        try {
            FaqRequest faqRequest = new FaqRequest();
            BeanUtils.copyProperties(faq,faqRequest);
            FaqResponse faqResponse = faqService.updateFaq(faqRequest, httpServletRequest);
            if (faqResponse.getResponseCode().equals(ApiResponseCode.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", faqResponse.getMessage());
                return "redirect:/app/faq";
            } else {
                model.addAttribute("faq",faq);
                model.addAttribute("failure", faqResponse.getMessage());
                logger.error("Error creating content", faqResponse.getMessage());
                return "faq/edit";
            }
        }catch (PaaroException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());

            logger.error("Error creating content",ibe);
            return "faq/edit";
        }
    }

    @GetMapping("/{id}/delete")
    public String editBank(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model){
        FaqResponse faqResponse = faqService.deleteFaq(id,httpServletRequest);
        if (faqResponse.getResponseCode().equals(ApiResponseCode.SUCCESSFUL)) {
            redirectAttributes.addFlashAttribute("message", faqResponse.getMessage());
            return "redirect:/app/faq";
        } else {
            model.addAttribute("failure", faqResponse.getMessage());
            logger.error("Error creating content", faqResponse.getMessage());
            return "faq/view";
        }
    }
}
