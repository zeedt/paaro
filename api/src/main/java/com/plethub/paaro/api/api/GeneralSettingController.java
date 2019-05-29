package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apirequestmodel.GeneralSettingRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.GeneralSettingsResponse;
import com.plethub.paaro.core.models.GeneralSetting;
import com.plethub.paaro.core.appservice.services.GeneralSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/settings")
public class GeneralSettingController {

    @Autowired
    private GeneralSettingService generalSettingService;

    @ResponseBody
    @PreAuthorize(value = "hasAnyAuthority('UPDATE_GENERAL_SETTINGS')")
    @RequestMapping("/create-or-update")
    public GeneralSettingsResponse createOrUpdateGeneralSettings(@RequestBody GeneralSetting generalSetting, HttpServletRequest httpServletRequest) {

        return generalSettingService.createOrUpdateGeneralSettings(generalSetting, httpServletRequest);

    }

    @ResponseBody
    @PreAuthorize(value = "hasAnyAuthority('APPROVE_GENERAL_SETTINGS')")
    @RequestMapping("/verify")
    public GeneralSettingsResponse verifyGeneralSettings(@RequestBody GeneralSettingRequest generalSettingRequest, HttpServletRequest httpServletRequest) {

        return generalSettingService.verifyGeneralSettings(generalSettingRequest, httpServletRequest);

    }

    @ResponseBody
    @PreAuthorize(value = "hasAnyAuthority('APPROVE_GENERAL_SETTINGS')")
    @RequestMapping("/decline")
    public GeneralSettingsResponse declineGeneralSettings(@RequestBody GeneralSettingRequest generalSettingRequest, HttpServletRequest httpServletRequest) {

        return generalSettingService.declineGeneralSettings(generalSettingRequest, httpServletRequest);

    }


}
