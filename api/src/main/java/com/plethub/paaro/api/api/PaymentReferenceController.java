package com.plethub.paaro.api.api;

import com.plethub.paaro.core.appservice.apiresponsemodel.PaymentReferenceResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.services.PaymentReferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment-reference")
public class PaymentReferenceController {

    @Autowired
    private PaymentReferenceService paymentReferenceService;

    private Logger logger = LoggerFactory.getLogger(PaymentReferenceController.class.getName());

    @GetMapping("/generate")
    public PaymentReferenceResponseModel generatePaymentRefrenceForUser(@RequestParam("currency") String currency, @RequestParam("paaroBankId") Long paaroBankId) {

        try {
            return paymentReferenceService.generatePaymentRefrenceForUser(currency, paaroBankId);
        } catch (Exception e) {
            logger.error("Error occurred while generating payment reference due to ", e);
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while generating payment reference");
        }

    }
    @GetMapping("/get-by-reference-number")
    public PaymentReferenceResponseModel getPaymentReferenceByRefNumber(@RequestParam("referenceNumber") String referenceNumber) {

        try {
            return paymentReferenceService.getPaymentReferenceByRefNumber(referenceNumber);
        } catch (Exception e) {
            logger.error("Error occurred while fetching payment reference due to ", e);
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching payment reference");
        }

    }

    //    @GetMapping("/get-by-currency")
    public PaymentReferenceResponseModel getPaymentReferenceByWallet(@RequestParam("currency") String currency, @RequestParam(value = "pageNo", required = false) Integer pageNo, @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        try {
            return paymentReferenceService.getPaymentReferenceByWallet(currency, pageNo, pageSize);
        } catch (Exception e) {
            logger.error("Error occurred while fetching payment reference by currency due to ", e);
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching payment reference by currency");
        }

    }

    @GetMapping("/get-by-currency-for-logged-in-user")
    public PaymentReferenceResponseModel getPaymentReferenceByLogggedInUserWallet(@RequestParam("currency") String currency, @RequestParam(value = "pageNo", required = false) Integer pageNo, @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        try {
            return paymentReferenceService.getPaymentReferenceByLogggedInUserWallet(currency, pageNo, pageSize);
        } catch (Exception e) {
            logger.error("Error occurred while fetching payment reference by currency due to ", e);
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching payment reference by currency");
        }

    }

    @GetMapping("/get-for-logged-in-user")
    public PaymentReferenceResponseModel getPaymentReferenceForLogggedInUser(@RequestParam(value = "pageNo", required = false) Integer pageNo, @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        try {
            return paymentReferenceService.getPaymentReferenceForLogggedInUser(pageNo, pageSize);
        } catch (Exception e) {
            logger.error("Error occurred while fetching payment reference due to ", e);
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching payment reference ");
        }

    }
    @GetMapping("/get-pending-for-logged-in-user")
    public PaymentReferenceResponseModel getPendingPaymentReferenceForLogggedInUser(@RequestParam(value = "pageNo", required = false) Integer pageNo, @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        try {
            return paymentReferenceService.getPendingPaymentReferenceForLogggedInUser(pageNo, pageSize);
        } catch (Exception e) {
            logger.error("Error occurred while fetching payment pending reference due to ", e);
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching pending payment reference");
        }

    }

    @GetMapping("/cancel-bid")
    public PaymentReferenceResponseModel cancelBid(@RequestParam("bidId") Long bidId) {
        try {
            return paymentReferenceService.cancelBid(bidId);
        } catch (Exception e) {
            logger.error("Error occurred while fetching payment pending reference due to ", e);
            return PaymentReferenceResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while cancelling bid");
        }
    }

}
