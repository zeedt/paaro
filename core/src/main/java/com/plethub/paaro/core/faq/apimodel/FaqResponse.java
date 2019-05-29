package com.plethub.paaro.core.faq.apimodel;

import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.Faq;
import org.springframework.data.domain.Page;

import java.util.List;

public class FaqResponse {

    private ApiResponseCode responseCode;

    private String message;

    private Faq faq;

    private List<Faq> faqList;

    private Page<Faq> faqPage;

    public Faq getFaq() {
        return faq;
    }

    public void setFaq(Faq faq) {
        this.faq = faq;
    }

    public List<Faq> getFaqList() {
        return faqList;
    }

    public void setFaqList(List<Faq> faqList) {
        this.faqList = faqList;
    }

    public Page<Faq> getFaqPage() {
        return faqPage;
    }

    public void setFaqPage(Page<Faq> faqPage) {
        this.faqPage = faqPage;
    }

    public static FaqResponse fromCodeAndNarration(ApiResponseCode apiResponseCode, String message) {

        FaqResponse faqResponse = new FaqResponse();
        faqResponse.setMessage(message);
        faqResponse.setResponseCode(apiResponseCode);

        return faqResponse;
    }

    public ApiResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ApiResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
