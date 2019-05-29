package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apirequestmodel.KycRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.KycResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.services.KycService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/kyc")
public class KycController {

    private Logger logger = LoggerFactory.getLogger(KycController.class.getName());


    @Autowired
    private KycService kycService;

    @PostMapping("/upload-bvn-or-identification-number")
    public KycResponseModel uploadBvn(@RequestBody KycRequestModel kycRequestModel) {

        try {
            return kycService.uploadBvnOrIdentificationNumber(kycRequestModel);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred during bvn validation/upload");
        }

    }

    @PostMapping("/upload-utility-bill")
    public KycResponseModel uploadUtilityBill(@RequestBody KycRequestModel kycRequestModel) {

        try {
            return kycService.uploadUtilityBill(kycRequestModel);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred during utility bill upload");
        }

    }

    @PostMapping("/upload-valid-id")
    public KycResponseModel uploadValidId(@RequestBody KycRequestModel kycRequestModel) {

        try {
            return kycService.uploadValidId(kycRequestModel);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred during valid id upload");
        }

    }

    @PostMapping("/upload-passport-photo")
    public KycResponseModel uploadPassportPhoto(@RequestBody KycRequestModel kycRequestModel) {

        try {
            return kycService.uploadPassportPhoto(kycRequestModel);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred during passport photo upload");
        }

    }

    @GetMapping("/get-customer-kyc-by-currency")
    public KycResponseModel getCustomerKycInfo(@RequestParam("currencyType") String currencyType) {

        try {
            return kycService.getUserKycByCurrency(currencyType);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching user kyc");
        }

    }

    @GetMapping("/get-customer-kyc-flag")
    public KycResponseModel getCustomerKycFlag(@RequestParam("currencyType") String currencyType) {

        try {
            return kycService.getUserKycFlagByCurrency(currencyType);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching user kyc");
        }

    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_OR_DECLINE_KYC')")
    @GetMapping("/verifyBvnKyc")
    public KycResponseModel verifyBvnKyc(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {
            return kycService.verifyBvnKyc(id, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying bvn kyc");
        }
    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_OR_DECLINE_KYC')")
    @GetMapping("/declineBvnKyc")
    public KycResponseModel declineBvnKyc(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {
            return kycService.declineBvnKyc(id, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while declining bvn kyc");
        }
    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_OR_DECLINE_KYC')")
    @GetMapping("/verifyValidIdKyc")
    public KycResponseModel verifyValidIdKyc(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {
            return kycService.verifyValidIdKyc(id, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying valid id kyc");
        }
    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_OR_DECLINE_KYC')")
    @GetMapping("/declineValidIdKyc")
    public KycResponseModel declineValidIdKyc(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {
            return kycService.declineValidIdKyc(id, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while declining valid id kyc");
        }
    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_OR_DECLINE_KYC')")
    @GetMapping("/verifyPassportKyc")
    public KycResponseModel verifyPassportKyc(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {
            return kycService.verifyPassportKyc(id, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying passport kyc");
        }
    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_OR_DECLINE_KYC')")
    @GetMapping("/declinePassportKyc")
    public KycResponseModel declinePassportKyc(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {
            return kycService.declinePassportKyc(id, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while declining passport kyc");
        }
    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_OR_DECLINE_KYC')")
    @GetMapping("/verifyUtilityBillKyc")
    public KycResponseModel verifyUtilityBillKyc(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {
            return kycService.verifyUtilityBillKyc(id, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying utility bill kyc");
        }
    }

    @PreAuthorize(value = "hasAnyAuthority('VERIFY_OR_DECLINE_KYC')")
    @GetMapping("/declineUtilityBillKyc")
    public KycResponseModel declineUtilityBillKyc(@RequestParam("id") Long id, HttpServletRequest servletRequest) {

        try {
            return kycService.declineUtilityBillKyc(id, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred due to ", e);
            return KycResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while declining utility bill kyc");
        }
    }

    @GetMapping("/download-kyc-as-attachment")
    public void downloadKycAsAttachment(@RequestParam("id") Long id, @RequestParam("kycType") String kycType, HttpServletResponse servletResponse) {

        try {
            kycService.downloadKycAsAttachment(servletResponse, id, kycType);
        } catch (Exception e) {
            logger.error("Error occurred while downloading kyc due to ", e);
        }
    }
}
