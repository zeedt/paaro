package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.appservice.apirequestmodel.BankRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankResponseModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.CurrencyResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.BankType;
import com.plethub.paaro.core.authority.services.AuthorityService;
import com.plethub.paaro.core.models.Authority;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.apimodels.ManagedUserModelApi;
import com.plethub.paaro.core.usermanagement.apimodels.UserUpdateRequestModel;
import com.plethub.paaro.core.usermanagement.enums.ResponseStatus;
import com.plethub.paaro.core.usermanagement.enums.UserCategory;
import com.plethub.paaro.core.usermanagement.service.UserService;
import com.plethub.paaro.webapp.ApiModel.UserDetails;
import com.plethub.paaro.webapp.dto.ChangePassword;
import com.plethub.paaro.webapp.dto.LoginForm;
import com.plethub.paaro.webapp.exception.PaaroException;
import com.plethub.paaro.webapp.service.AuthService;
import org.apache.commons.lang3.math.NumberUtils;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.ws.Binding;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/app/admin")
public class AdminController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserService userService;

    @Autowired
    AuthorityService authorityService;


    @ModelAttribute
    public void init(Model model) {
        //model.addAttribute("faqs", faqService.getFaqs());
        //userService.
    }

    @GetMapping
    public String getAdminUsers() {
        return "admin/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<ManagedUser> getUsers(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ManagedUser> sq = userService.getAdminUserDetails(pageable).getManagedUserPage();
        DataTablesOutput<ManagedUser> out = new DataTablesOutput<ManagedUser>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping("/new")
    public String addAdminUsers(Model model) {
        ManagedUser managedUser = new ManagedUser();
        managedUser.setUserCategory(UserCategory.ADMIN);
        model.addAttribute("managedUser", managedUser);
        return "admin/add";
    }

    @PostMapping
    public String createAdminUsers(@Valid ManagedUserModelApi managedUserModelApi, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "admin/add";
        }

        try {

            ManagedUserModelApi message = userService.createAdminUser(managedUserModelApi, httpServletRequest);
            if (message.getResponseStatus().equals(ResponseStatus.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", message.getMessage());
                return "redirect:/app/admin";
            } else {
                model.addAttribute("managedUser", managedUserModelApi);
                model.addAttribute("failure", message.getMessage());
                logger.error("Error creating content", message.getMessage());
                return "admin/add";
            }

        } catch (PaaroException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            model.addAttribute("managedUser", managedUserModelApi);
            model.addAttribute("failure", ibe.getMessage());
            logger.error("Error creating content", ibe);
            return "admin/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editFaq(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            model.addAttribute("managedUser", userService.getUserModelById(id, httpServletRequest).getManagedUser());
            return "admin/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/admin";
        }

    }

    @PostMapping("/update")
    public String updateAdminUser(@Valid ManagedUserModelApi managedUserModelApi, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "admin/edit";
        }

        try {

            ManagedUserModelApi message = userService.updateAdminUser(managedUserModelApi, httpServletRequest);
            if (message.getResponseStatus().equals(ResponseStatus.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", message.getMessage());
                return "redirect:/app/admin";
            } else {
                model.addAttribute("managedUser", managedUserModelApi);
                model.addAttribute("failure", message.getMessage());
                logger.error("Error creating content", message.getMessage());
                return "admin/edit";
            }

        } catch (PaaroException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());
            model.addAttribute("managedUser", managedUserModelApi);
            logger.error("Error creating content", ibe);
            return "admin/edit";
        }
    }

    @GetMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            ManagedUser user = userService.getUserModelById(id, httpServletRequest).getManagedUser();
            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            if (user.isActive()) {
                managedUserModelApi = userService.deactivateUser(user.getEmail(), httpServletRequest);
            } else {
                managedUserModelApi = userService.activateUser(user.getEmail(), httpServletRequest);
            }
            redirectAttributes.addFlashAttribute("message", managedUserModelApi.getMessage());
            return "redirect:/app/admin";
        } catch (Exception e) {
            model.addAttribute("failure", e.getMessage());
            return "admin/view";
        }
    }

    @GetMapping("/{id}/enable")
    public String enableCurrency(@PathVariable Long id, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            ManagedUser user = userService.getUserModelById(id, httpServletRequest).getManagedUser();
            ManagedUserModelApi managedUserModelApi = userService.activateUser(user.getEmail(), httpServletRequest);
            redirectAttributes.addFlashAttribute("message", managedUserModelApi.getMessage());
            return "redirect:/app/admin";
        } catch (Exception e) {
            model.addAttribute("failure", e.getMessage());
            return "admin/view";
        }
    }

    @GetMapping("/{id}/disable")
    public String disableCurrency(@PathVariable Long id, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            ManagedUser user = userService.getUserModelById(id, httpServletRequest).getManagedUser();
            ManagedUserModelApi managedUserModelApi = userService.deactivateUser(user.getEmail(), httpServletRequest);
            redirectAttributes.addFlashAttribute("message", managedUserModelApi.getMessage());
            return "redirect:/app/admin";
        } catch (Exception e) {
            model.addAttribute("failure", e.getMessage());
            return "admin/view";
        }
    }

    @GetMapping("/{id}/authority")
    public String manageAuthorities(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            ManagedUserModelApi managedUser = userService.getUserModelById(id, httpServletRequest);
            model.addAttribute("managedUser", managedUser.getManagedUser());
            model.addAttribute("userAuthorities", managedUser.getAuthorityList());
            model.addAttribute("nonUserAuthorities", authorityService.fetchUnMappedAuthoritiesForUser(managedUser.getManagedUser().getId()).getAuthorityList());
            model.addAttribute("authorityList", authorityService.getAllAuthorities(httpServletRequest));
            return "admin/authority";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/admin";
        }

    }

    @PostMapping("/authority")
    public String mapAuthorities(ManagedUser managedUser, RedirectAttributes redirectAttributes, Model model, WebRequest request, HttpServletRequest httpServletRequest, Locale locale) {

        try {
            ManagedUserModelApi managedUserModelApi = userService.getUserModelById(managedUser.getId(), httpServletRequest);
            logger.info("Auth {}", managedUserModelApi.toString());
            List<String> authorityList = new ArrayList<>();

            String[] permissions = request.getParameterValues("permissionsList");
            if (permissions != null) {
                for (String perm : permissions) {
                    authorityList.add(perm);
                }
            }
            ManagedUserModelApi response = authorityService.mapAuthoritiesToUser(managedUserModelApi.getManagedUser().getEmail(), authorityList, httpServletRequest);
            if (response.getResponseStatus().equals(ResponseStatus.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", response.getMessage());
                return "redirect:/app/admin";
            } else {
                model.addAttribute("managedUser", managedUserModelApi.getManagedUser());
                model.addAttribute("userAuthorities", managedUserModelApi.getAuthorityList());
                model.addAttribute("nonUserAuthorities", authorityService.fetchUnMappedAuthoritiesForUser(managedUserModelApi.getManagedUser().getId()).getAuthorityList());
                model.addAttribute("authorityList", authorityService.getAllAuthorities(httpServletRequest));
                model.addAttribute("failure", response.getMessage());
                logger.error("Error creating content", response.getMessage());
                return "admin/authority";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/admin";
        }
    }

}
