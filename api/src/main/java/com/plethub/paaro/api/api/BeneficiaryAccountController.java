package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apirequestmodel.BeneficiaryAccountRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BeneficiaryAccountResponseModel;
import com.plethub.paaro.core.appservice.services.BeneficiaryAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("beneficiary-account")
public class BeneficiaryAccountController {

    @Autowired
    private BeneficiaryAccountService beneficiaryAccountService;

    @PostMapping("/add")
    public BeneficiaryAccountResponseModel addBeneficiary(@RequestBody BeneficiaryAccountRequestModel beneficiaryAccountRequestModel) {

        return beneficiaryAccountService.addBeneficiaryAccountToUser(beneficiaryAccountRequestModel);
    }

    @GetMapping("/get-for-logged-in-user")
    public BeneficiaryAccountResponseModel getBeneficiary() {

        return beneficiaryAccountService.getBeneficiaryAccountForUser();
    }

    @GetMapping("/get-for-logged-in-user-by-currency")
    public BeneficiaryAccountResponseModel getBeneficiaryByCurrency(@RequestParam("currencyType") String currencyType) {

        return beneficiaryAccountService.getBeneficiaryAccountForUserByCurrency(currencyType);
    }

    @GetMapping("/delete")
    public BeneficiaryAccountResponseModel deleteBeneficiaryAccount(@RequestParam("id") Long id) {

        return beneficiaryAccountService.deleteBeneficiaryForUser(id);

    }

}
