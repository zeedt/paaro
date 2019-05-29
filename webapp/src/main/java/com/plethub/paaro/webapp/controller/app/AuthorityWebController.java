package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import com.plethub.paaro.core.authority.services.AuthorityService;
import com.plethub.paaro.core.faq.apimodel.FaqRequest;
import com.plethub.paaro.core.faq.apimodel.FaqResponse;
import com.plethub.paaro.core.faq.service.FaqService;
import com.plethub.paaro.core.models.Faq;
import com.plethub.paaro.webapp.exception.PaaroException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
@RequestMapping("/app/authority")
public class AuthorityWebController {

    @Autowired
    AuthorityService authorityService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void init(HttpServletRequest httpServletRequest, Model model) {
        model.addAttribute("authorities", authorityService.getAllAuthorities(httpServletRequest).getAuthorityList());
    }

    @GetMapping
    public String all(Model model){
        return "authority/view";
    }
}
