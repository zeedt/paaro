package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apirequestmodel.CurrencyDetailsRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.CurrencyDetailsresponse;
import com.plethub.paaro.core.appservice.services.CurrencyDetailsService;
import com.plethub.paaro.core.models.CurrencyDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/currency-details")
public class CurrencyDetailsController {

    @Autowired
    private CurrencyDetailsService currencyDetailsService;

    @GetMapping(value = "/all")
    public CurrencyDetailsresponse fetchAllCurrencyDetails() {
        return currencyDetailsService.getAllCurrencydetails();
    }


    @GetMapping(value = "/by-code")
    public CurrencyDetailsresponse fetchAllCurrencyDetails(@RequestParam("currencyCode") String currencyCode) {
        return currencyDetailsService.getCurrencydetailsByCode(currencyCode);
    }

    @PreAuthorize(value = "hasAnyAuthority('ADD_CURRENCY')")
    @PostMapping("/add-currency-details")
    public CurrencyDetailsresponse addCurrencyDetailsForCurrency(@RequestBody CurrencyDetailsRequest currencyDetailsRequest, HttpServletRequest httpServletRequest) {

        return currencyDetailsService.addCurrencyDetailsForCurrency(currencyDetailsRequest, httpServletRequest);

    }

    @PreAuthorize(value = "hasAnyAuthority('UPDATE_CURRENCY')")
    @PostMapping("/update-currency-details")
    public CurrencyDetailsresponse updateCurrencyDetailsForCurrency(@RequestBody CurrencyDetailsRequest currencyDetailsRequest, HttpServletRequest httpServletRequest) {

        return currencyDetailsService.updateCurrencyDetailsForCurrency(currencyDetailsRequest, httpServletRequest);

    }

    @GetMapping("/gbp-average-exchange-rate")
    public CurrencyDetailsresponse getGbpAverageConversionRate() {

        return currencyDetailsService.getGbpAverageConversionRate();

    }

}
