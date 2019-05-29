package com.plethub.paaro.core.appservice.apiresponsemodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.PaymentReference;
import org.springframework.data.domain.Page;

public class PaymentReferenceResponseModel {

    private ApiResponseCode apiResponseCode;

    private String message;

    private PaymentReference paymentReference;

    private Page<PaymentReference> paymentReferencePage;

    public ApiResponseCode getApiResponseCode() {
        return apiResponseCode;
    }

    public void setApiResponseCode(ApiResponseCode apiResponseCode) {
        this.apiResponseCode = apiResponseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PaymentReference getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(PaymentReference paymentReference) {
        this.paymentReference = paymentReference;
    }


    public static PaymentReferenceResponseModel fromNarration(ApiResponseCode apiResponseCode, String message) {
        PaymentReferenceResponseModel paymentReferenceResponseModel = new PaymentReferenceResponseModel();
        paymentReferenceResponseModel.setMessage(message);
        paymentReferenceResponseModel.setApiResponseCode(apiResponseCode);

        return paymentReferenceResponseModel;
    }

    public Page<PaymentReference> getPaymentReferencePage() {
        return paymentReferencePage;
    }

    public void setPaymentReferencePage(Page<PaymentReference> paymentReferencePage) {
        this.paymentReferencePage = paymentReferencePage;
    }
}
