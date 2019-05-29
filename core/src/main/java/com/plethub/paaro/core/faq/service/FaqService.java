package com.plethub.paaro.core.faq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.repository.FaqRepository;
import com.plethub.paaro.core.faq.apimodel.FaqRequest;
import com.plethub.paaro.core.faq.apimodel.FaqResponse;
import com.plethub.paaro.core.models.Faq;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FaqService {

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private AuditLogService auditLogService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger = LoggerFactory.getLogger(FaqService.class.getName());

    @Transactional
    public FaqResponse addFaq(FaqRequest faqRequest, HttpServletRequest servletRequest) {

        try {
            FaqResponse faqResponse = validateFaqRequest(faqRequest);

            if (faqResponse != null) return faqResponse;

            return saveFaqAndAudit(faqRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while adding FAQ due to ", e);
            return FaqResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while adding FAQ");
        }

    }

    @Transactional
    public FaqResponse updateFaq(FaqRequest faqRequest, HttpServletRequest servletRequest) {

        try {
            FaqResponse validationResponse = validateFaqRequestForUpdate(faqRequest);

            if (validationResponse != null && validationResponse.getResponseCode() != ApiResponseCode.SUCCESSFUL) return validationResponse;

            return updateFaqAndAudit(faqRequest, validationResponse.getFaq(),  servletRequest);

        } catch (Exception e) {
            logger.error("Error occurred while updating FAQ due to ", e);
            return FaqResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while updating FAQ");
        }

    }

    @Transactional
    public FaqResponse deleteFaq(Long faqId, HttpServletRequest servletRequest) {

        try {
            Faq faq = faqRepository.findOne(faqId);

            if (faq == null) return FaqResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Faq not found");

            return deleteFaqAndAudit(faq, servletRequest);

        } catch (Exception e) {
            logger.error("Error occurred while deleting FAQ due to ", e);
            return FaqResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while deleting FAQ");
        }

    }

    private FaqResponse saveFaqAndAudit(FaqRequest faqRequest, HttpServletRequest servletRequest) throws JsonProcessingException {

        Faq faq = new Faq();
        faq.setAnswer(faqRequest.getAnswer());
        faq.setQuestion(faqRequest.getQuestion());
        faq.setUpdatedDate(new Date());

        faqRepository.save(faq);

        auditLogService.saveAudit(null, objectMapper.writeValueAsString(faq), Module.FAQ, servletRequest, "User added FAQ", faq.getId() );

        return FaqResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User added FAQ successfully");
    }

    private FaqResponse updateFaqAndAudit(FaqRequest faqRequest, Faq existingFaq, HttpServletRequest servletRequest) throws JsonProcessingException {

        Faq oldFaq = existingFaq;

        existingFaq.setAnswer(faqRequest.getAnswer());
        existingFaq.setQuestion(faqRequest.getQuestion());
        existingFaq.setUpdatedDate(new Date());

        faqRepository.save(existingFaq);

        auditLogService.saveAudit(objectMapper.writeValueAsString(oldFaq), objectMapper.writeValueAsString(existingFaq), Module.FAQ, servletRequest, "User updated FAQ", existingFaq.getId() );

        return FaqResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User updated FAQ successfully");
    }

    private FaqResponse deleteFaqAndAudit(Faq faq,  HttpServletRequest servletRequest) throws JsonProcessingException { ;

        faqRepository.delete(faq);

        auditLogService.saveAudit(objectMapper.writeValueAsString(faq), null, Module.FAQ, servletRequest, "User deleted FAQ", faq.getId() );

        return FaqResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User deleted FAQ successfully");
    }

    private FaqResponse validateFaqRequest(FaqRequest faqRequest) {

        if (faqRequest == null) return FaqResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Request object cannot be null");

        if (StringUtils.isEmpty(faqRequest.getQuestion()) || StringUtils.isEmpty(faqRequest.getAnswer()))
            return FaqResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Question and answer cannot be blank");


        Faq faq = faqRepository.findTopByQuestion(faqRequest.getQuestion().trim());

        if (faq != null) return FaqResponse.fromCodeAndNarration(ApiResponseCode.ALREADY_EXIST, "Question already exist in Faq. Kindly update if necessary");

        return null;

    }

    private FaqResponse validateFaqRequestForUpdate(FaqRequest faqRequest) {

        if (faqRequest == null) return FaqResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Request object cannot be null");

        if (faqRequest.getId() == null || StringUtils.isEmpty(faqRequest.getQuestion()) || StringUtils.isEmpty(faqRequest.getAnswer()))
            return FaqResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Question and answer cannot be blank. Also, ID cannot be null");


        Faq faq = faqRepository.findOne(faqRequest.getId());

        if (faq == null) return FaqResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Faq not found");


        if (!faq.getQuestion().trim().equalsIgnoreCase(faqRequest.getQuestion().trim())) {
            Faq questionFaq = faqRepository.findTopByQuestion(faqRequest.getQuestion().trim());

            if (questionFaq != null) return FaqResponse.fromCodeAndNarration(ApiResponseCode.ALREADY_EXIST, "Question already exist for another Faq. Kindly update if necessary");
        }

        FaqResponse faqResponse = new FaqResponse();
        faqResponse.setResponseCode(ApiResponseCode.SUCCESSFUL);
        faqResponse.setFaq(faq);

        return faqResponse;

    }

    public FaqResponse getFaqs(Integer pageNo, Integer pageSize) {

        pageNo = (pageNo == null) ? 0 : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? 15 : pageSize;

        Page<Faq> faqPage = faqRepository.findAllByIdNotNull(new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        FaqResponse faqResponse = new FaqResponse();
        faqResponse.setResponseCode(ApiResponseCode.SUCCESSFUL);
        faqResponse.setFaqPage(faqPage);

        return faqResponse;
    }

    public FaqResponse getFaqs(Pageable pageable) {
        Page<Faq> faqPage = faqRepository.findAll(pageable);

        FaqResponse faqResponse = new FaqResponse();
        faqResponse.setResponseCode(ApiResponseCode.SUCCESSFUL);
        faqResponse.setFaqPage(faqPage);
        return faqResponse;
    }

    public List<Faq> getFaqs() {
        return faqRepository.findAll();
    }

    public FaqResponse getFaq(Long id) {

        Faq faq = faqRepository.findOne(id);

        FaqResponse faqResponse = new FaqResponse();
        faqResponse.setResponseCode(ApiResponseCode.SUCCESSFUL);
        faqResponse.setFaq(faq);

        return faqResponse;
    }

}
