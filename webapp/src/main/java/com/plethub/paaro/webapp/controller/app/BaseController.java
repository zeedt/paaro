package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.appservice.apiresponsemodel.WalletBalanceDashboardDetails;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.services.TransactionSearchService;
import com.plethub.paaro.core.appservice.services.TransferService;
import com.plethub.paaro.core.appservice.services.WalletService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import com.plethub.paaro.webapp.ApiModel.UserDetails;
import com.plethub.paaro.webapp.dto.ChangePassword;
import com.plethub.paaro.webapp.exception.PaaroException;
import com.plethub.paaro.webapp.service.AuthService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;

@Controller
public class BaseController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    AuthService authService;

    @Autowired
    public UserService userService;

    @Autowired
    WalletService walletService;

    @Autowired
    private TransactionSearchService transactionSearchService;

    @GetMapping("/")
    public String base(){
        return "redirect:/app/dashboard";
    }

    @GetMapping(value = "/app/dashboard")
    public String dashboard(HttpServletRequest hsr, RedirectAttributes redirectAttributes, Model model){

        model.addAttribute("signups", userService.getDashboardDetailsForSignUps());
        model.addAttribute("transactions", transactionSearchService.getDashboardDetailsForTransactions());
        model.addAttribute("revenue", transactionSearchService.getDashboardDetailsForRevenue());
        model.addAttribute("balance", walletService.getWalletBalanceDashboardDetails());
        return "dashboard";
    }

    @GetMapping("/app/profile")
    public String changePassword(HttpServletRequest hsr, Model model){

        UserDetails principal = authService.getCurrentUser(hsr);
        model.addAttribute("changePassword", new ChangePassword());
        return "change-pword";
    }

    @PostMapping("/app/change-pword")
    public String changePasswordProcess(@Valid ChangePassword changePassword, BindingResult result, Model model, Locale locale, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes){

        if(result.hasErrors()){
            result.addError(new ObjectError("invalid",messageSource.getMessage("form.fields.required",null,locale)));
            return "change-pword";
        }

        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())){
            model.addAttribute("failure", "New Password should be the same as Confirm Password");
            return "change-pword";
        }

        try {
            String message = authService.changePassword(changePassword, httpServletRequest.getSession());

            redirectAttributes.addFlashAttribute("message", message);
            return "redirect:/app/change-pword";
        }catch (PaaroException ibe){
            result.addError(new ObjectError("error",ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());

            logger.error("Error creating content",ibe);
            return "change-pword";
        }
    }

    @GetMapping("/app/logout")
    public String logout(HttpServletRequest session){

//        UserDetails userDetails = authService.getCurrentUser(session);
//        if (null != userDetails){
//            System.out.println("going to the service");
//            authService.logout(userDetails);
//        }

        try {
            SecurityContextHolder.getContext().setAuthentication(null);
        } finally {
            return "redirect:/login";
        }

    }
}
