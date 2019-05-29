package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.services.PaaroUserService;
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
import java.security.Principal;

@Controller
@RequestMapping("/app/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaaroUserService paaroUserService;

    @ResponseBody
    @PreAuthorize(value = "hasAnyAuthority('DEACTIVATE_USER')")
    @RequestMapping (value = "/deactivateUser", method = RequestMethod.GET)
    public ManagedUserModelApi deactivateUser(Principal principal, @RequestParam("email") String email, HttpServletRequest servletRequest) {
        return userService.deactivateUser(email, servletRequest);
    }

    @ResponseBody
    @PreAuthorize(value = "hasAnyAuthority('ACTIVATE_USER')")
    @RequestMapping (value = "/activateUser", method = RequestMethod.GET)
    public ManagedUserModelApi activateUser(Principal principal, @RequestParam("email") String email, HttpServletRequest servletRequest) {
        return userService.activateUser(email, servletRequest);
    }

    @ResponseBody
    @RequestMapping (value = "/updateUser", method = RequestMethod.POST)
    public ManagedUserModelApi updateUser(Principal principal, @RequestBody UserUpdateRequestModel requestModel, HttpServletRequest servletRequest) {
        return userService.updateUser(requestModel, servletRequest);
    }

    @ResponseBody
    @RequestMapping (value = "/reset_user_password", method = RequestMethod.POST)
    public ManagedUserModelApi resetUserPassword(Principal principal, @RequestBody PasswordResetRequestModel requestModel, HttpServletRequest servletRequest) {
        return userService.resetUserPassword(requestModel, servletRequest);
    }

    @ResponseBody
    @RequestMapping (value = "/getUserDetailsByEmail", method = RequestMethod.GET)
    public ManagedUserModelApi resetUserPassword(@RequestParam("email") String email, HttpServletRequest servletRequest) throws Exception {
        return userService.getUserModelByEmailOrDisplayName(email, servletRequest);
    }


    @ResponseBody
    @RequestMapping (value = "/login", method = RequestMethod.POST)
    public Object login(@RequestBody ManagedUserModelApi managedUserModelApi, HttpServletRequest servletRequest) throws Exception {
        return paaroUserService.login(managedUserModelApi, servletRequest);
    }

    @ResponseBody
    @PreAuthorize(value = "hasAnyAuthority('CREATE_ADMIN_USER')")
    @RequestMapping (value = "/createAdminUser", method = RequestMethod.POST)
    public ManagedUserModelApi resetUserPassword(@RequestBody ManagedUserModelApi managedUserModelApi, HttpServletRequest servletRequest) throws Exception {
        return userService.createAdminUser(managedUserModelApi, servletRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/upload-image", method = RequestMethod.POST)
    public ManagedUserModelApi uploadOrUpdateImage(@RequestBody ManagedUser managedUser) {

        return userService.updateOrUploadImage(managedUser);

    }


}
