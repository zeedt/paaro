package com.plethub.paaro.core.usermanagement.controller;


import com.plethub.paaro.core.appservice.apiresponsemodel.UserDashboardDetails;
import com.plethub.paaro.core.usermanagement.apimodels.ManagedUserModelApi;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.apimodels.PasswordResetRequestModel;
import com.plethub.paaro.core.usermanagement.apimodels.UserUpdateRequestModel;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserManagementController {

    @Autowired
    public UserService userService;

    @ResponseBody
    @PreAuthorize("hasAuthority('VIEW_USER_DETAILS')")
    @RequestMapping(value = "/getDetailsByemail")
    public ManagedUserModelApi getUserInformation(@RequestParam String email, HttpServletRequest servletRequest) throws Exception {
        return userService.getUserModelByEmailOrDisplayName(email, servletRequest);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('VIEW_USER_DETAILS')")
    @RequestMapping(value = "/get-details-by-email-or-display-name")
    public ManagedUserModelApi getUserInformationByEmailOrDisplayName(@RequestParam String email, HttpServletRequest servletRequest) throws Exception {
        return userService.getUserModelByEmailOrDisplayName(email, servletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public ManagedUserModelApi createUser(@RequestBody ManagedUserModelApi managedUserModelApi, HttpServletRequest servletRequest){
        return userService.addManagedUser(managedUserModelApi, servletRequest);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('DEACTIVATE_USER')")
    @RequestMapping (value = "/deactivateUser", method = RequestMethod.GET)
    public ManagedUserModelApi deactivateUser(@RequestParam("email") String email, Principal principal, HttpServletRequest httpServletRequest) {
        return userService.deactivateUser(email, httpServletRequest);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('ACTIVATE_USER')")
    @RequestMapping (value = "/activateUser", method = RequestMethod.GET)
    public ManagedUserModelApi activateUser(@RequestParam("email") String email, HttpServletRequest servletRequest) {
        return userService.activateUser(email,  servletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/reset_user_password", method = RequestMethod.POST)
    public ManagedUserModelApi resetUserPassword(@RequestBody PasswordResetRequestModel resetRequestModel, HttpServletRequest servletRequest) {
        return userService.resetUserPassword(resetRequestModel, servletRequest);
    }


    @ResponseBody
    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public ManagedUserModelApi updateUserDetails(@RequestBody UserUpdateRequestModel requestModel, HttpServletRequest servletRequest){
        return userService.updateUser(requestModel, servletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/forgotPassword", method = RequestMethod.POST)
    public ManagedUserModelApi forgotPassword(@RequestBody ManagedUserModelApi requestModel, HttpServletRequest servletRequest) throws IOException {
        return userService.forgotPassword(requestModel, servletRequest);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('CREATE_ADMIN_USER')")
    @RequestMapping(value = "/createAdminUser", method = RequestMethod.POST)
    public ManagedUserModelApi createAdminUser(@RequestBody ManagedUserModelApi managedUserModelApi, HttpServletRequest servletRequest){
        return userService.createAdminUser(managedUserModelApi, servletRequest);
    }

    @ResponseBody
    @RequestMapping (value = "/logout", method = RequestMethod.GET)
    public ManagedUserModelApi logout(HttpServletRequest servletRequest) {
        return userService.logout(servletRequest);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('VIEW_USER_DETAILS')")
    @RequestMapping (value = "/fetch-customers-list", method = RequestMethod.GET)
    public ManagedUserModelApi fetchCustomerUserDetails(@RequestParam(value = "pageNo", required = false) Integer pageNo, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.fetchCustomerUserDetails(pageNo, pageSize);
    }

    @ResponseBody
    @PreAuthorize("hasAuthority('VIEW_USER_DETAILS')")
    @RequestMapping (value = "/fetch-admin-list", method = RequestMethod.GET)
    public ManagedUserModelApi fetchAdminUserDetails(@RequestParam(value = "pageNo", required = false) Integer pageNo, @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        return userService.fetchAdminUserDetails(pageNo, pageSize);
    }

    @ResponseBody
    @RequestMapping (value = "/fetch-signup-details-for-dashboard", method = RequestMethod.GET)
    public UserDashboardDetails getDashboardDetailsForSignUps() {
        return userService.getDashboardDetailsForSignUps();
    }

    @ResponseBody
    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    public String activateAccountWithLink(@RequestParam("email") String email, @RequestParam("activationCode") String activationCode) {
        return userService.activateUserAccountWithEmail(email, activationCode);
    }

}
