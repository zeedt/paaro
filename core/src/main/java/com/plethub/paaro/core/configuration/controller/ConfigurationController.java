package com.plethub.paaro.core.configuration.controller;

import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.configuration.apimodel.ActionResponse;
import com.plethub.paaro.core.configuration.apimodel.ConfigurationResponse;
import com.plethub.paaro.core.configuration.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

    @Autowired
    private ConfigurationService configurationService;


    @PreAuthorize("hasAuthority('EDIT_CONFIGURATION')")
    @GetMapping("/enable-or-disable-maker-checker-for-action")
    public ConfigurationResponse toggleMakerChecker(@RequestParam("action") Action action, @RequestParam("enable") boolean enable) {

        return configurationService.toggleMakerCheckerForAction(action, enable);

    }


    @PreAuthorize("hasAuthority('VIEW_CONFIGURATION')")
    @GetMapping("/get-configuration-by-action")
    public ConfigurationResponse getConfigurationForAction(@RequestParam("action") Action action) {

        return configurationService.getConfigurationForAction(action);

    }

    @PreAuthorize("hasAuthority('VIEW_CONFIGURATION')")
    @GetMapping("/get-configurations-by-page")
    public ConfigurationResponse getConfigurationListByPage(@RequestParam("pageNo") Integer pageNo, @RequestParam("pageSize") Integer pageSize) {

        return configurationService.getConfigurationListByPage(pageNo, pageSize);

    }


    @GetMapping("/get-actions")
    public ActionResponse getActionsList () {

        return configurationService.getActionList();

    }

    @GetMapping("/get-possible-pending-actions")
    public ActionResponse getPossiblePendingActionsList () {

        return configurationService.getPossiblePendingActionList();

    }

    @GetMapping("/get-possible-verification-actions")
    public ActionResponse getPossibleVerificationActionsList () {

        return configurationService.getPossibleVerificationActionList();

    }




}
