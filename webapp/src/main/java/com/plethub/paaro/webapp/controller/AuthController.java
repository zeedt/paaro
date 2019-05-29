package com.plethub.paaro.webapp.controller;

import com.plethub.paaro.webapp.ApiModel.UserDetails;
import com.plethub.paaro.webapp.dto.ForgotPassword;
import com.plethub.paaro.webapp.dto.LoginForm;
import com.plethub.paaro.webapp.exception.PaaroException;
import com.plethub.paaro.webapp.service.AuthService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Locale;

@Controller
@RequestMapping("/login")
public class AuthController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    AuthService authService;

    @GetMapping
    public String login(Model model){
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }


    @PostMapping("/app")
    private String auth(@Valid LoginForm loginForm, BindingResult result, Model model, HttpSession httpSession, RedirectAttributes redirectAttributes, Locale locale){

        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "login";
        }

        try {
            String message = authService.login(loginForm, httpSession);

//            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/app/dashboard";
        }catch (PaaroException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());

            logger.error("Error creating content",ibe);
            return "login";
        }

    }

    @GetMapping("/reset-pword")
    public String resetPassword(Model model){
        model.addAttribute("loginForm", new LoginForm());
        return "recover";
    }

    @GetMapping("/forgot-pword")
    public String forgotPassword(Model model){
        model.addAttribute("forgotPassword", new ForgotPassword());
        return "forgot";
    }

    @PostMapping("/forgot-pword")
    public String submitForgotPassword(@Valid ForgotPassword forgotPassword, BindingResult result, Model model, RedirectAttributes redirectAttributes, Locale locale){

        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "forgot";
        }
        try {
            String message = authService.forgotPassword(forgotPassword);
            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/login/forgot-pword";
        }catch (PaaroException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());
            logger.error("Error creating content",ibe);
            return "forgot";
        }
    }

}
