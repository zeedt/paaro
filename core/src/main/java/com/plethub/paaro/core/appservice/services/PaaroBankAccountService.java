package com.plethub.paaro.core.appservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.apirequestmodel.PaaroBankAccountRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.PaaroBankResponseModel;
import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.Status;
import com.plethub.paaro.core.appservice.repository.CurrencyRepository;
import com.plethub.paaro.core.appservice.repository.RequestRepository;
import com.plethub.paaro.core.configuration.service.ConfigurationService;
import com.plethub.paaro.core.models.Currency;
import com.plethub.paaro.core.models.PaaroBankAccount;
import com.plethub.paaro.core.appservice.repository.PaaroBankAccountRepository;
import com.plethub.paaro.core.models.Request;
import com.plethub.paaro.core.request.apimodel.RequestResponse;
import com.plethub.paaro.core.request.service.RequestService;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class PaaroBankAccountService {

    @Autowired
    private PaaroBankAccountRepository paaroBankAccountRepository;

    @Autowired
    private RequestService requestService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private Logger logger  = LoggerFactory.getLogger(PaaroBankAccountService.class.getName());

    @Autowired
    private ConfigurationService configurationService;

    public PaaroBankResponseModel getActivePaaroBankAccount() {
        List<PaaroBankAccount> paaroBankAccounts = paaroBankAccountRepository.findAllByActiveIsTrue();

        if (CollectionUtils.isEmpty(paaroBankAccounts)) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No active account found");
        }

        PaaroBankResponseModel paaroBankResponseModel = new PaaroBankResponseModel();
        paaroBankResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paaroBankResponseModel.setMessage("Active account found");
        paaroBankResponseModel.setPaaroBankAccounts(paaroBankAccounts);
        return paaroBankResponseModel;

    }

    public PaaroBankResponseModel getAllPaaroBankAccount() {
        List<PaaroBankAccount> paaroBankAccounts = paaroBankAccountRepository.findAll();

        if (CollectionUtils.isEmpty(paaroBankAccounts)) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No active account found");
        }

        PaaroBankResponseModel paaroBankResponseModel = new PaaroBankResponseModel();
        paaroBankResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paaroBankResponseModel.setMessage("");
        paaroBankResponseModel.setPaaroBankAccounts(paaroBankAccounts);
        return paaroBankResponseModel;

    }

    public PaaroBankResponseModel getPaaroBankAccountById(Long id) {


        if (id == null) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Id cannot be null");
        }

        PaaroBankAccount paaroBankAccount = paaroBankAccountRepository.findOne(id);

        if (paaroBankAccount == null) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No account found");
        }

        PaaroBankResponseModel paaroBankResponseModel = new PaaroBankResponseModel();
        paaroBankResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paaroBankResponseModel.setMessage("");
        paaroBankResponseModel.setPaaroBankAccount(paaroBankAccount);
        return paaroBankResponseModel;

    }

    public PaaroBankResponseModel getActivePaaroBankAccountByCurrencyType(String currencyType) {

        if (StringUtils.isEmpty(currencyType)) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }


        List<PaaroBankAccount> paaroBankAccounts = paaroBankAccountRepository.findAllByCurrency_TypeAndActiveIsTrue(currencyType);

        if (CollectionUtils.isEmpty(paaroBankAccounts)) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No active account found for ");
        }

        PaaroBankResponseModel paaroBankResponseModel = new PaaroBankResponseModel();
        paaroBankResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paaroBankResponseModel.setMessage("Active account(s) found");
        paaroBankResponseModel.setPaaroBankAccounts(paaroBankAccounts);
        return paaroBankResponseModel;

    }

    public PaaroBankResponseModel getPaaroBankAccountByCurrencyType(String currencyType) {

        if (StringUtils.isEmpty(currencyType)) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }


        List<PaaroBankAccount> paaroBankAccounts = paaroBankAccountRepository.findAllByCurrency_Type(currencyType);

        if (CollectionUtils.isEmpty(paaroBankAccounts)) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No active account found for ");
        }

        PaaroBankResponseModel paaroBankResponseModel = new PaaroBankResponseModel();
        paaroBankResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paaroBankResponseModel.setMessage("Active account(s) found");
        paaroBankResponseModel.setPaaroBankAccounts(paaroBankAccounts);
        return paaroBankResponseModel;

    }

    public PaaroBankResponseModel createPaaroBankAccount(PaaroBankAccountRequest paaroBankAccountRequest, HttpServletRequest servletRequest) {

        try {
            PaaroBankResponseModel validationResponse = validateNewPaaroBankAccount(paaroBankAccountRequest);

            if (validationResponse != null && validationResponse.getApiResponseCode() != ApiResponseCode.SUCCESSFUL) return validationResponse;

            if (configurationService.isMakerCheckerEnabledForAction(Action.CREATE_PAARO_BANK_ACCOUNT)) return createNewPaaroBankAccountRequest(paaroBankAccountRequest, servletRequest);

            return createNewPaaroBankAccountAndAudit(paaroBankAccountRequest, validationResponse.getCurrency(), servletRequest);

        } catch (Exception e) {
            logger.error("Error occurred while creating paaro bank account due to ", e);
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while creating paaro bank account");
        }

    }

    public PaaroBankResponseModel updatePaaroBankAccount(PaaroBankAccountRequest paaroBankAccountRequest, HttpServletRequest servletRequest) {

        try {
            PaaroBankResponseModel validationResponse = validatePaaroBankAccountForUpdate(paaroBankAccountRequest);

            if (validationResponse != null && validationResponse.getApiResponseCode() != ApiResponseCode.SUCCESSFUL) return validationResponse;

            if (requestService.isRequestPending(Action.UPDATE_PAARO_BANK_ACCOUNT, validationResponse.getPaaroBankAccount().getId())) return PaaroBankResponseModel.fromNarration(ApiResponseCode.PENDING_VERIFICATION, "Item pending verification");

            if (configurationService.isMakerCheckerEnabledForAction(Action.UPDATE_PAARO_BANK_ACCOUNT)) return createPaaroBankAccountUpdateRequest(paaroBankAccountRequest, validationResponse.getPaaroBankAccount(), servletRequest);

            return updatePaaroBankAccountAndAudit(paaroBankAccountRequest, validationResponse.getPaaroBankAccount(), validationResponse.getCurrency(), servletRequest);

        } catch (Exception e) {
            logger.error("Error occurred while updating paaro bank account due to ", e);
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while updating paaro bank account");
        }

    }

    private PaaroBankResponseModel validatePaaroBankAccountForUpdate(PaaroBankAccountRequest paaroBankAccountRequest) {
        if (paaroBankAccountRequest == null) return PaaroBankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST,
                "Paaro bank account cannot be null");

        if (paaroBankAccountRequest.getId() == null || StringUtils.isEmpty(paaroBankAccountRequest.getAccountName()) || StringUtils.isEmpty(paaroBankAccountRequest.getBankName())
                 || StringUtils.isEmpty(paaroBankAccountRequest.getCurrencyType())) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST,
                    "Id, Bank name, Acccount name and currency type cannot be blank");
        }

        Currency currency = currencyRepository.findCurrencyByType(paaroBankAccountRequest.getCurrencyType().trim());

        if (currency == null) return PaaroBankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Specified currency type not found");

        PaaroBankAccount existingPaaroBankAccount = paaroBankAccountRepository.findOne(paaroBankAccountRequest.getId());

        if (existingPaaroBankAccount == null) return PaaroBankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND,
                "Paaro Bank account not found");

        if ( existingPaaroBankAccount.getCurrency().getId() == currency.getId() && existingPaaroBankAccount.getActive() == paaroBankAccountRequest.getActive()
                && existingPaaroBankAccount.getAccountName().trim().equalsIgnoreCase(paaroBankAccountRequest.getAccountName().trim())
                && existingPaaroBankAccount.getBankName().trim().equalsIgnoreCase(paaroBankAccountRequest.getBankName().trim())
                && existingPaaroBankAccount.getAccountNumber().trim().equalsIgnoreCase(paaroBankAccountRequest.getAccountNumber().trim())
                ) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.NO_MODIFICATION, "Nothing to update");
        }

        PaaroBankResponseModel paaroBankResponseModel = new PaaroBankResponseModel();
        paaroBankResponseModel.setCurrency(currency);
        paaroBankAccountRequest.setCurrency(currency);
        paaroBankResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        paaroBankResponseModel.setPaaroBankAccount(paaroBankAccountRequest);

        return paaroBankResponseModel;

    }

    private PaaroBankResponseModel createPaaroBankAccountUpdateRequest(PaaroBankAccountRequest paaroBankAccountRequest, PaaroBankAccount existingPaaroBankAccount, HttpServletRequest servletRequest) throws JsonProcessingException {

        paaroBankAccountRequest.setAccountNumber(existingPaaroBankAccount.getAccountNumber());
        paaroBankAccountRequest.setDateAdded(existingPaaroBankAccount.getDateAdded());

        String message = requestService.logRequest(existingPaaroBankAccount.getId(), Action.UPDATE_PAARO_BANK_ACCOUNT, objectMapper.writeValueAsString(existingPaaroBankAccount)
                , objectMapper.writeValueAsString(paaroBankAccountRequest));

        if (!StringUtils.isEmpty(message)) return PaaroBankResponseModel.fromNarration(ApiResponseCode.FAILED, message);

        auditLogService.saveAudit(objectMapper.writeValueAsString(existingPaaroBankAccount), objectMapper.writeValueAsString(paaroBankAccountRequest)
                , Module.PAARO_BANK_ACCOUNT,servletRequest, "User updated paaro bank account for approval", existingPaaroBankAccount.getId());

        return PaaroBankResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Paaro account update request logged successfully");


    }

    private PaaroBankResponseModel createNewPaaroBankAccountAndAudit(PaaroBankAccountRequest paaroBankAccountRequest, Currency currency, HttpServletRequest servletRequest) throws JsonProcessingException {


        PaaroBankAccount paaroBankAccount = new PaaroBankAccount();
        BeanUtils.copyProperties(paaroBankAccountRequest, paaroBankAccount);
        paaroBankAccount.setDateAdded(new Date());
        paaroBankAccount.setActive(true);
        paaroBankAccount.setCurrency(currency);

        paaroBankAccountRepository.save(paaroBankAccount);

        auditLogService.saveAudit(null, objectMapper.writeValueAsString(paaroBankAccount), Module.PAARO_BANK_ACCOUNT, servletRequest, "User added paaro bank account", paaroBankAccount.getId());

        return PaaroBankResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Paaro bank account successfully created");
    }

    private PaaroBankResponseModel updatePaaroBankAccountAndAudit(PaaroBankAccountRequest paaroBankAccountRequest, PaaroBankAccount existingPaaroBankAccount, Currency currency, HttpServletRequest servletRequest) throws JsonProcessingException {


        PaaroBankAccount paaroBankAccount = new PaaroBankAccount();
        BeanUtils.copyProperties(paaroBankAccountRequest, paaroBankAccount);
        paaroBankAccount.setDateUpdated(new Date());
        paaroBankAccount.setDateAdded(existingPaaroBankAccount.getDateAdded());
        paaroBankAccount.setAccountNumber(existingPaaroBankAccount.getAccountNumber());

        paaroBankAccountRepository.save(paaroBankAccount);

        auditLogService.saveAudit(objectMapper.writeValueAsString(existingPaaroBankAccount), objectMapper.writeValueAsString(paaroBankAccount), Module.PAARO_BANK_ACCOUNT, servletRequest, "User updated paaro bank account", existingPaaroBankAccount.getId());

        return PaaroBankResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Paaro bank account successfully updated");
    }

    private PaaroBankResponseModel createNewPaaroBankAccountRequest(PaaroBankAccountRequest paaroBankAccountRequest, HttpServletRequest servletRequest) throws JsonProcessingException {
        String message = requestService.logRequest(null, objectMapper.writeValueAsString(paaroBankAccountRequest), Action.CREATE_PAARO_BANK_ACCOUNT, null );

        if (!StringUtils.isEmpty(message)) return PaaroBankResponseModel.fromNarration(ApiResponseCode.FAILED, message);

        auditLogService.saveAudit(null, objectMapper.writeValueAsString(paaroBankAccountRequest), Module.PAARO_BANK_ACCOUNT, servletRequest, "User logged request for new paaro bank account");

        return PaaroBankResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "New account logged for verification");
    }

    private PaaroBankResponseModel validateNewPaaroBankAccount(PaaroBankAccountRequest paaroBankAccountRequest) {

        if (paaroBankAccountRequest == null) return PaaroBankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST,
                "Paaro bank account cannot be null");

        if (StringUtils.isEmpty(paaroBankAccountRequest.getAccountName()) || StringUtils.isEmpty(paaroBankAccountRequest.getBankName())
                || StringUtils.isEmpty(paaroBankAccountRequest.getAccountNumber()) || StringUtils.isEmpty(paaroBankAccountRequest.getCurrencyType())) {
            return PaaroBankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Bank name, Acccount name, Account number and currency type cannot be blank");
        }

        Currency currency = currencyRepository.findCurrencyByType(paaroBankAccountRequest.getCurrencyType().trim());

        if (currency == null) return PaaroBankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Specified currency type not found");

        PaaroBankAccount existingPaaroBankAccount = paaroBankAccountRepository.findTopByAccountNumber(paaroBankAccountRequest.getAccountNumber());

        if (existingPaaroBankAccount != null) return PaaroBankResponseModel.fromNarration(ApiResponseCode.ALREADY_EXIST,
                "Bank account with same account number already exist");

        PaaroBankResponseModel paaroBankResponseModel = new PaaroBankResponseModel();
        paaroBankResponseModel.setCurrency(currency);
        paaroBankResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        return paaroBankResponseModel;

    }

    public RequestResponse verifyBankCreationRequest(Request request, HttpServletRequest servletRequest) {

        try {
            PaaroBankAccountRequest bankAccountRequest = objectMapper.readValue(request.getNewData(), PaaroBankAccountRequest.class);
            PaaroBankResponseModel validationResponse = validateNewPaaroBankAccount(bankAccountRequest);
            if (validationResponse.getApiResponseCode() != ApiResponseCode.SUCCESSFUL) return RequestResponse.fromCodeAndNarration(validationResponse.getApiResponseCode(), validationResponse.getMessage());


            PaaroBankAccount paaroBankAccount = new PaaroBankAccount();
            BeanUtils.copyProperties(bankAccountRequest, paaroBankAccount);
            paaroBankAccount.setDateAdded(new Date());
            paaroBankAccount.setActive(true);
            paaroBankAccount.setCurrency(validationResponse.getCurrency());

            paaroBankAccount = paaroBankAccountRepository.save(paaroBankAccount);

            request.setVerifierId(userService.getCurrentLoggedInUser());
            request.setStatus(Status.VERIFIED);
            request.setVerifiedOrDeclinedDate(new Date());
            request.setEntityId(paaroBankAccount.getId());
            requestRepository.save(request);

            auditLogService.saveAudit(null,objectMapper.writeValueAsString(paaroBankAccount), Module.PAARO_BANK_ACCOUNT, servletRequest, "User successfully verified paaro bank account creation", paaroBankAccount.getId());

            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Paaro bank account creation successfully verified");

        } catch (IOException e) {
            logger.error("System error occurred while verifying paaro bank account creation due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying paaro bank account creation");
        }
    }

    public RequestResponse verifyBankUpdateRequest(Request request, HttpServletRequest servletRequest) {

        try {
            if (request.getEntityId() == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Entity Id not found");
            PaaroBankAccountRequest bankAccountRequest = objectMapper.readValue(request.getNewData(), PaaroBankAccountRequest.class);

            if (bankAccountRequest == null || bankAccountRequest.getId() == null || bankAccountRequest.getId().longValue() != request.getEntityId().longValue()) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

            PaaroBankAccount paaroBankAccount = new PaaroBankAccount();
            BeanUtils.copyProperties(bankAccountRequest, paaroBankAccount);
            paaroBankAccount.setDateUpdated(new Date());

            paaroBankAccount = paaroBankAccountRepository.save(paaroBankAccount);


            request.setVerifierId(userService.getCurrentLoggedInUser());
            request.setStatus(Status.VERIFIED);
            request.setVerifiedOrDeclinedDate(new Date());
            request.setEntityId(paaroBankAccount.getId());
            requestRepository.save(request);

            auditLogService.saveAudit(null,objectMapper.writeValueAsString(paaroBankAccount), Module.PAARO_BANK_ACCOUNT, servletRequest, "User successfully verified paaro bank account update", paaroBankAccount.getId());

            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Paaro bank account update successfully verified");

        } catch (Exception e) {
            logger.error("System error occurred while verifying paaro bank account update due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while verifying paaro bank account update");
        }
    }
}
