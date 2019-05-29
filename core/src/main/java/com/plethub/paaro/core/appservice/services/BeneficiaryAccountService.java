package com.plethub.paaro.core.appservice.services;

import com.plethub.paaro.core.appservice.apirequestmodel.BeneficiaryAccountRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BeneficiaryAccountResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.models.BeneficiaryAccount;
import com.plethub.paaro.core.models.Currency;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import com.plethub.paaro.core.appservice.repository.BeneficiaryAccountRepository;
import com.plethub.paaro.core.appservice.repository.CurrencyRepository;
import com.plethub.paaro.core.models.ManagedUser;
//import com.plethub.paaro.core.security.UserDetailsTokenEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class BeneficiaryAccountService {

    @Autowired
    private BeneficiaryAccountRepository beneficiaryAccountRepository;

    private Logger logger = LoggerFactory.getLogger(BeneficiaryAccountService.class.getName());

    @Autowired
    BankRepository bankRepository;

    @Autowired
    private BankService bankService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Value("${currency.naira-type:NGN}")
    private String nairaCurrency;

    public BeneficiaryAccountResponseModel addBeneficiaryAccountToUser(BeneficiaryAccountRequestModel beneficiaryAccountRequestModel) {

        try {
            BeneficiaryAccountResponseModel validationError = validateBeneficiaryAccount(beneficiaryAccountRequestModel);

            if (validationError != null) {
                return validationError;
            }

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null) {
                return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "No user is currently logged in");
            }

            BeneficiaryAccount existingBeneficiaryAccount = beneficiaryAccountRepository.findAllByBank_IdAndUserIdAndAccountNumber(beneficiaryAccountRequestModel.getBankId(), userDetailsTokenEnvelope.getManagedUser().getId(), beneficiaryAccountRequestModel.getAccountNumber().trim());

            if (existingBeneficiaryAccount != null) {
                return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.ALREADY_EXIST, "Beneficiary already exist");
            }

            if (!nairaCurrency.trim().equalsIgnoreCase(beneficiaryAccountRequestModel.getCurrencyType().trim()) && StringUtils.isEmpty(beneficiaryAccountRequestModel.getSortCode()))
                return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Sort code required for non naira account");

            BeneficiaryAccount beneficiaryAccount = new BeneficiaryAccount();
            beneficiaryAccount.setAccountNumber(beneficiaryAccountRequestModel.getAccountNumber());
            beneficiaryAccount.setAccountName(beneficiaryAccountRequestModel.getAccountName());
            beneficiaryAccount.setBank(beneficiaryAccountRequestModel.getBank());
            beneficiaryAccount.setUserId(userDetailsTokenEnvelope.getManagedUser().getId());
            beneficiaryAccount.setCurrency(beneficiaryAccountRequestModel.getCurrencyType());
            beneficiaryAccount.setSortCode(beneficiaryAccountRequestModel.getSortCode());

            beneficiaryAccountRepository.save(beneficiaryAccount);

            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Beneficiary successfully added");
        } catch (Exception e) {
            logger.error("Error occurred while saving beneficiary account due to ", e);
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "Error occurred while saving beneficiary occurred");
        }

    }

    public BeneficiaryAccountResponseModel getBeneficiaryAccountForUser() {

        try {
            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null) {
                return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "No user is currently logged in");
            }

            List<BeneficiaryAccount> existingBeneficiaryAccount = beneficiaryAccountRepository.findAllByUserId(userDetailsTokenEnvelope.getManagedUser().getId());


            BeneficiaryAccountResponseModel beneficiaryAccountResponseModel = new BeneficiaryAccountResponseModel();
            beneficiaryAccountResponseModel.setResponseCode(ApiResponseCode.SUCCESSFUL);
            beneficiaryAccountResponseModel.setBeneficiaryAccountList(existingBeneficiaryAccount);

            return beneficiaryAccountResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching beneficiary account due to ", e);
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "Error occurred while fetching beneficiary");
        }

    }


    public BeneficiaryAccountResponseModel getBeneficiaryAccountForUserByCurrency(String currency) {

        try {
            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null) {
                return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "No user is currently logged in");
            }

            List<BeneficiaryAccount> beneficiaryAccounts = beneficiaryAccountRepository.findAllByUserIdAndCurrency(userDetailsTokenEnvelope.getManagedUser().getId(), currency);


            BeneficiaryAccountResponseModel beneficiaryAccountResponseModel = new BeneficiaryAccountResponseModel();
            beneficiaryAccountResponseModel.setResponseCode(ApiResponseCode.SUCCESSFUL);
            beneficiaryAccountResponseModel.setBeneficiaryAccountList(beneficiaryAccounts);

            return beneficiaryAccountResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching user beneficiary account by currency due to ", e);
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "Error occurred while fetching user beneficiary by currency");
        }

    }


    private BeneficiaryAccountResponseModel validateBeneficiaryAccount(BeneficiaryAccountRequestModel beneficiaryAccountRequestModel) {

        if (beneficiaryAccountRequestModel == null) {
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid request object");
        }

        if (StringUtils.isEmpty(beneficiaryAccountRequestModel.getAccountNumber())) {
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Account number cannot be blank");
        }

        if (StringUtils.isEmpty(beneficiaryAccountRequestModel.getBankId())) {
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Bank id cannot be blank");
        }

        if (StringUtils.isEmpty(beneficiaryAccountRequestModel.getCurrencyType())) {
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency type cannot be blank");
        }

        Bank bank = bankRepository.getOne(beneficiaryAccountRequestModel.getBankId());

        if (bank == null) {
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Bank not found with the Id supplied");
        }

        Currency currency = currencyRepository.findCurrencyByType(beneficiaryAccountRequestModel.getCurrencyType());

        if (currency == null) {
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Currency not found");
        }

        String accountValidationResponse;

        if (beneficiaryAccountRequestModel.getCurrencyType().equalsIgnoreCase(nairaCurrency)) {
            accountValidationResponse = bankService.validateAccountWithFlutterwave(beneficiaryAccountRequestModel.getAccountNumber().trim(), bank);
        } else {
            if (StringUtils.isEmpty(beneficiaryAccountRequestModel.getAccountName()))
                return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Account name for foreign account cannot be blank");
            accountValidationResponse = beneficiaryAccountRequestModel.getAccountName();
        }

        if (accountValidationResponse == null) {
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Unable to validate account number");
        }

        beneficiaryAccountRequestModel.setAccountName(accountValidationResponse);
        beneficiaryAccountRequestModel.setBank(bank);

        return null;

    }

    public BeneficiaryAccountResponseModel deleteBeneficiaryForUser(Long id) {
        try {
            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ManagedUser managedUser = userDetailsTokenEnvelope.getManagedUser();
            if (managedUser == null) {
                return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "No user details found");
            }

            BeneficiaryAccount beneficiaryAccount = beneficiaryAccountRepository.findTopByIdAndUserId(id, managedUser.getId());

            if (beneficiaryAccount == null) {
                return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.NOT_FOUND, "Beneficiary account not found for logged in user");
            }

            beneficiaryAccountRepository.delete(beneficiaryAccount);

            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.SUCCESSFUL, "Beneficiary account deleted successfully for customer");
        } catch (Exception e) {
            logger.error("Error occurred while deleting beneficiary for user");
            return BeneficiaryAccountResponseModel.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while deleting beneficiary for customer");
        }

    }




}
