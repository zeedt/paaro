package com.plethub.paaro.core.appservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.apirequestmodel.BankRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankResponseModel;
import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.BankType;
import com.plethub.paaro.core.appservice.enums.Status;
import com.plethub.paaro.core.appservice.repository.RequestRepository;
import com.plethub.paaro.core.configuration.service.ConfigurationService;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import com.plethub.paaro.core.appservice.thirdparty.models.flutterwave.AccountValidationResponse;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class BankService {

    @Autowired
    private BankRepository bankRepository;

    @Value("${flutter-wave.account.validation:https://ravesandboxapi.flutterwave.com/flwv3-pug/getpaidx/api/resolve_account}")
    private String accountValidationEndpoint;

    @Value("${flutter-wave.api.key:FLWPUBK-ed439274dc425f4f86c08d9c7627da26-X}")
    private String pBFPubKey;

    private Logger logger = LoggerFactory.getLogger(BankService.class.getName());


    private RestTemplate restTemplate;

    @Autowired
    private RequestService requestService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate();
    }

    public BankResponseModel fetchAllBanks() {

        try {
            List<Bank> bankList = bankRepository.findAll();

            BankResponseModel bankResponseModel = new BankResponseModel();
            bankResponseModel.setBankList(bankList);
            bankResponseModel.setMessage("All banks fetched");
            bankResponseModel.setResponseCode(ApiResponseCode.SUCCESSFUL);

            return bankResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all banks due to ", e);
            return BankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "Unable to fetch all banks record");
        }
    }

    public BankResponseModel fetchAllForeignBanks() {

        try {
            List<Bank> bankList = bankRepository.findAllByBankType(BankType.FOREIGN);

            BankResponseModel bankResponseModel = new BankResponseModel();
            bankResponseModel.setBankList(bankList);
            bankResponseModel.setMessage("All foreign banks fetched");
            bankResponseModel.setResponseCode(ApiResponseCode.SUCCESSFUL);

            return bankResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all foreign banks due to ", e);
            return BankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "Unable to fetch all foreign banks record");
        }
    }

    public BankResponseModel fetchPagedForeignBanks(Integer pageNo, Integer pageSize) {

        try {
            pageNo = pageNo == null || pageNo < 0 ? 0 : pageNo;
            pageSize = pageSize == null || pageSize < 1 ? 15 : pageSize;
            Page<Bank> bankPage = bankRepository.findAllByBankType(BankType.FOREIGN, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

            BankResponseModel bankResponseModel = new BankResponseModel();
            bankResponseModel.setBankPage(bankPage);
            bankResponseModel.setMessage("Paged foreign banks fetched");
            bankResponseModel.setResponseCode(ApiResponseCode.SUCCESSFUL);

            return bankResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching paged foreign banks due to ", e);
            return BankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "Unable to fetch paged foreign banks");
        }
    }

    public BankResponseModel fetchPagedLocalBanks(Integer pageNo, Integer pageSize) {

        try {
            pageNo = pageNo == null || pageNo < 0 ? 0 : pageNo;
            pageSize = pageSize == null || pageSize < 1 ? 15 : pageSize;
            Page<Bank> bankPage = bankRepository.findAllByBankType(BankType.LOCAL, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

            BankResponseModel bankResponseModel = new BankResponseModel();
            bankResponseModel.setBankPage(bankPage);
            bankResponseModel.setMessage("Paged local banks fetched");
            bankResponseModel.setResponseCode(ApiResponseCode.SUCCESSFUL);

            return bankResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching paged local banks due to ", e);
            return BankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "Unable to fetch paged local banks");
        }
    }

    public BankResponseModel fetchAllLocalBanks() {

        try {
            List<Bank> bankList = bankRepository.findAllByBankType(BankType.LOCAL);

            BankResponseModel bankResponseModel = new BankResponseModel();
            bankResponseModel.setBankList(bankList);
            bankResponseModel.setMessage("All local banks fetched");
            bankResponseModel.setResponseCode(ApiResponseCode.SUCCESSFUL);

            return bankResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all local banks due to ", e);
            return BankResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "Unable to fetch all local banks record");
        }
    }

    public BankResponseModel validateAccountNumber(BankRequestModel bankRequestModel) {

        if (bankRequestModel == null || StringUtils.isEmpty(bankRequestModel.getAccountNumber())) {
            return BankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Account number must be passed");
        }

        if (bankRequestModel.getBankId() == null) {
            return BankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Bank Id must be passed");
        }

        Bank bank = bankRepository.findOne(bankRequestModel.getBankId());
        if (bank == null) {
            return BankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Bank not found");
        }

        String accountName = validateAccountWithFlutterwave(bankRequestModel.getAccountNumber(), bank);

        if (accountName == null) {
            return BankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Account not fond");
        }

        BankResponseModel bankResponseModel = new BankResponseModel();
        bankResponseModel.setAccountNumber(bankRequestModel.getAccountNumber());
        bankResponseModel.setAccountName(accountName);

        return bankResponseModel;

    }

    public BankResponseModel addBank(BankRequestModel bankRequestModel, HttpServletRequest servletRequest) {

        try {
            if (bankRequestModel == null) {
                return BankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");
            }

            Bank bank = new Bank();
            BeanUtils.copyProperties(bankRequestModel,bank);
            BankResponseModel bankResponseModel = validateBank(bank);
            if (bankResponseModel != null) return bankResponseModel;

            if (configurationService.isMakerCheckerEnabledForAction(Action.ADD_BANK)) return createNewBankRequestAndAudit(bank, servletRequest);


            return saveBankAndAudit(bank, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while creating new bank request due to ", e);
            return BankResponseModel.fromNarration(
                    ApiResponseCode.SYSTEM_ERROR, "System error occurred while creating bank"
            );
        }

    }

    public BankResponseModel updateBank (BankRequestModel bankRequestModel, HttpServletRequest servletRequest) {

        try {
            if (bankRequestModel == null)
                return BankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

            Bank bank = new Bank();
            BeanUtils.copyProperties(bankRequestModel,bank);
            BankResponseModel bankResponseModel = validateBankUpdate(bank);
            if (bankResponseModel != null && bankResponseModel.getResponseCode() != ApiResponseCode.SUCCESSFUL) return bankResponseModel;

            if (bankResponseModel == null) return BankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request");

            Bank existingBank = bankResponseModel.getBank();

            if (requestService.isRequestPending(Action.UPDATE_BANK, existingBank.getId())) return BankResponseModel.fromNarration(ApiResponseCode.PENDING_VERIFICATION, "Item is pending verification");
            if (configurationService.isMakerCheckerEnabledForAction(Action.UPDATE_BANK)) return createUpdateBankRequestAndAudit(bank, existingBank, servletRequest);

            return updateBankAndAudit(bank, existingBank, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while updating bank request due to ", e);
            return BankResponseModel.fromNarration(
                    ApiResponseCode.SYSTEM_ERROR, "System error occurred while updating bank"
            );
        }

    }

    private BankResponseModel createUpdateBankRequestAndAudit(Bank bank, Bank existingBank, HttpServletRequest servletRequest) throws JsonProcessingException {

        String message = requestService.logRequest(existingBank.getId(), Action.UPDATE_BANK, objectMapper.writeValueAsString(existingBank), objectMapper.writeValueAsString(bank));

        if (!StringUtils.isEmpty(message)) {
            return BankResponseModel.fromNarration(ApiResponseCode.FAILED, message);
        }

        auditLogService.saveAudit(objectMapper.writeValueAsString(existingBank), objectMapper.writeValueAsString(bank), Module.BANK, servletRequest, "User logged bank update request for verification", existingBank.getId());

        return BankResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Bank update request logged for verification");

    }

    private BankResponseModel createNewBankRequestAndAudit(Bank bank, HttpServletRequest servletRequest) throws JsonProcessingException {

        String message = requestService.logRequest(null, objectMapper.writeValueAsString(bank), Action.ADD_BANK, null);

        if (!StringUtils.isEmpty(message)) {
            return BankResponseModel.fromNarration(ApiResponseCode.FAILED, message);
        }

        auditLogService.saveAudit(null, objectMapper.writeValueAsString(bank), Module.BANK, servletRequest, "User logged new bank request for verification");

        return BankResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Bank creation request logged for verification");

    }

    private BankResponseModel saveBankAndAudit(Bank bank, HttpServletRequest servletRequest) throws JsonProcessingException {

        bank.setId(null);
        bank.setDisabled(false);
        bank = bankRepository.save(bank);

        auditLogService.saveAudit(null, objectMapper.writeValueAsString(bank), Module.BANK, servletRequest, "User added bank");

        return BankResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Bank successfully added");

    }

    private BankResponseModel updateBankAndAudit(Bank bank, Bank existingBank, HttpServletRequest servletRequest) throws JsonProcessingException {

        bank.setId(existingBank.getId());

        bankRepository.save(bank);

        auditLogService.saveAudit(objectMapper.writeValueAsString(existingBank), objectMapper.writeValueAsString(bank), Module.BANK, servletRequest, "User updated bank", existingBank.getId());

        return BankResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Bank successfully updated");

    }

    private BankResponseModel validateBank(Bank bank) {

        if (bank == null || StringUtils.isEmpty(bank.getBankCode()) || StringUtils.isEmpty(bank.getCountryCode()) || StringUtils.isEmpty(bank.getBankName()) || bank.getBankType() == null)
            return BankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Bank code, bank name, country code, bank type cannot be blank");


        Bank existingBank = bankRepository.findTopByBankCodeOrBankName(bank.getBankCode().trim(), bank.getBankName().trim());

        if (existingBank != null) return BankResponseModel.fromNarration(ApiResponseCode.ALREADY_EXIST, "Bank already exist with the bank code or bank name");

        return null;

    }

    private BankResponseModel validateBankUpdate(Bank bank) {

        if (bank == null || bank.getId() == null || StringUtils.isEmpty(bank.getBankCode()) || StringUtils.isEmpty(bank.getCountryCode()) || StringUtils.isEmpty(bank.getBankName()) || bank.getBankType() == null)
            return BankResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Bank Id, Bank code, bank name, country code, bank type cannot be blank");


        Bank existingBank = bankRepository.getOne(bank.getId());

        if (existingBank == null) return BankResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Bank does not exist");

        if (bank.getBankName().trim().equalsIgnoreCase(existingBank.getBankName().trim()) && bank.getBankCode().trim().equalsIgnoreCase(existingBank.getBankCode().trim())
                && bank.getCountryCode().trim().equalsIgnoreCase(existingBank.getCountryCode().trim()) && bank.getBankType() == existingBank.getBankType() && bank.isDisabled() == existingBank.isDisabled() ) {
            return BankResponseModel.fromNarration(ApiResponseCode.NO_MODIFICATION, "No changes made");
        }

        if (!existingBank.getBankCode().trim().equalsIgnoreCase(bank.getBankCode().trim())) {
            if (bankRepository.findTopByBankCode(bank.getBankCode()) != null) return BankResponseModel.fromNarration(ApiResponseCode.ALREADY_EXIST, "Another Bank already exist with the code");
        }

        if (!existingBank.getBankName().trim().equalsIgnoreCase(bank.getBankName().trim())) {
            if (bankRepository.findTopByBankName(bank.getBankName().trim()) != null) return BankResponseModel.fromNarration(ApiResponseCode.ALREADY_EXIST, "Another Bank already exist with the name");
        }

        BankResponseModel bankResponseModel = new BankResponseModel();
        bankResponseModel.setResponseCode(ApiResponseCode.SUCCESSFUL);
        bankResponseModel.setBank(existingBank);

        return bankResponseModel;

    }

    public String validateAccountWithFlutterwave(String accountNumber, Bank bank) {

        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            MultiValueMap<String, String> validateAccountRequest = new LinkedMultiValueMap<>();

            validateAccountRequest.add("recipientaccount", accountNumber);
            validateAccountRequest.add("destbankcode", bank.getBankCode());
            validateAccountRequest.add("PBFPubKey", pBFPubKey);

            AccountValidationResponse accountValidationResponse =  restTemplate.postForObject(accountValidationEndpoint, validateAccountRequest, AccountValidationResponse.class, httpHeaders);

            if (accountValidationResponse == null || accountValidationResponse.getData() == null
                    || accountValidationResponse.getData().getData() == null
                    || StringUtils.isEmpty(accountValidationResponse.getData().getData().getAccountname())
                    || StringUtils.isEmpty(accountValidationResponse.getData().getData().getAccountnumber())
                    ) {
                return null;
            }

            return accountValidationResponse.getData().getData().getAccountname();
        } catch (Exception e) {
            logger.error("Error occurred while validating account due to ", e);
        }

        return null;
    }

    public RequestResponse verifyBankCreationRequest(Request request, HttpServletRequest servletRequest) {

        try {
            Bank bank = objectMapper.readValue(request.getNewData(), Bank.class);
            if (bank == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Bank object cannot be null");
            BankResponseModel bankResponseModel = validateBank(bank);
            if (bankResponseModel != null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, bankResponseModel.getMessage());

            bank = bankRepository.save(bank);

            request.setVerifierId(userService.getCurrentLoggedInUser());
            request.setStatus(Status.VERIFIED);
            request.setVerifiedOrDeclinedDate(new Date());
            request.setEntityId(bank.getId());
            requestRepository.save(request);

            auditLogService.saveAudit(null, objectMapper.writeValueAsString(bank), Module.BANK, servletRequest, "User verified request", bank.getId());
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User successfully verified bank creation");

        } catch (Exception e) {
            logger.error("System error occurred due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred during bank verification");
        }

    }
    public RequestResponse verifyBankUpdateRequest(Request request, HttpServletRequest servletRequest) {

        try {
            Bank bank = objectMapper.readValue(request.getNewData(), Bank.class);
            if (bank == null || bank.getId() == null || bank.getId() != request.getEntityId()) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Bank object cannot be null");
            bank = bankRepository.save(bank);

            request.setVerifierId(userService.getCurrentLoggedInUser());
            request.setStatus(Status.VERIFIED);
            request.setVerifiedOrDeclinedDate(new Date());
            request.setEntityId(bank.getId());
            requestRepository.save(request);

            auditLogService.saveAudit(null, objectMapper.writeValueAsString(bank), Module.BANK, servletRequest, "User verified bank update request", bank.getId());
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "User successfully verified bank update request");

        } catch (Exception e) {
            logger.error("System error occurred due to ", e);
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred during bank update verification");
        }

    }
}
