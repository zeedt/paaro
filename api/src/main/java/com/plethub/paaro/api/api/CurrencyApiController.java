package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apirequestmodel.CurrencyRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.CurrencyResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.services.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/currency")
public class CurrencyApiController {

    private Logger logger = LoggerFactory.getLogger(CurrencyApiController.class.getName());

    @Autowired
    private CurrencyService currencyService;

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize(value = "hasAnyAuthority('ADD_CURRENCY')")
    public CurrencyResponse addCurrency(@RequestBody CurrencyRequest currencyRequest, HttpServletRequest servletRequest) {

        try {

            return currencyService.addCurrency(currencyRequest, servletRequest);

        }  catch (Exception e) {

            logger.error("Error occured while adding currency due to ", e);
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while adding currency");

        }
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/disable")
    @PreAuthorize(value = "hasAnyAuthority('DISABLE_CURRENCY')")
    public CurrencyResponse disableCurrency(@RequestParam("currencyType") String currecyType, HttpServletRequest servletRequest) {

        try {

            return currencyService.disableCurrency(currecyType, servletRequest);

        }  catch (Exception e) {
            logger.error("Error occured while disabling currency due to ", e);
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while disabling currency");

        }
    }
    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/enable")
    @PreAuthorize(value = "hasAnyAuthority('ENABLE_CURRENCY')")
    public CurrencyResponse enableCurrency(@RequestParam("currencyType") String currecyType, HttpServletRequest servletRequest) {

        try {

            return currencyService.enableCurrency(currecyType, servletRequest);

        }  catch (Exception e) {
            logger.error("Error occured while disabling currency due to ", e);
            return CurrencyResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while enabling currency");

        }
    }

}
