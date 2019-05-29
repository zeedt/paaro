package com.plethub.paaro.core.appservice.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plethub.paaro.core.appservice.apirequestmodel.CashOutRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.CashOutResponse;
import com.plethub.paaro.core.appservice.apiresponsemodel.CashOutValidationResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.CashOutStatus;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import com.plethub.paaro.core.appservice.repository.CashOutRepository;
import com.plethub.paaro.core.appservice.repository.WalletRepository;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.models.CashOutLog;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.models.Wallet;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CashOutService {
    
    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogService auditLogService;
    
    @Autowired
    private CashOutRepository cashOutRepository;

    @Autowired
    private BankRepository bankRepository;

    private Logger logger = LoggerFactory.getLogger(CashOutService.class.getName());

    private ObjectMapper objectMapper = new ObjectMapper();

    public CashOutLog getCashOut(Long id){
        return cashOutRepository.getOne(id);
    }

    public CashOutResponse logCashOutForCustomer(CashOutRequest cashOutRequest, HttpServletRequest servletRequest) throws JsonProcessingException {
        
        CashOutValidationResponse validationResponse = validateRequest(cashOutRequest);
        
        if (!StringUtils.isEmpty(validationResponse.getErrorMessage())) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, validationResponse.getErrorMessage());
        
        Wallet wallet = validationResponse.getWallet();
        BigDecimal newAvailableBalance = wallet.getAvailableAccountBalance().subtract(cashOutRequest.getAmount());
        wallet.setAvailableAccountBalance(newAvailableBalance);
        walletRepository.save(wallet);

        CashOutLog cashOutLog = new CashOutLog();
        cashOutLog.setAmount(cashOutRequest.getAmount());
        cashOutLog.setCustomerNarration(cashOutRequest.getNarration());
        cashOutLog.setRequestDate(new Date());
        cashOutLog.setWallet(wallet);
        cashOutLog.setAccountName(cashOutRequest.getAccountName());
        cashOutLog.setAccountNumber(cashOutRequest.getAccountNumber());
        cashOutLog.setBankId(cashOutRequest.getBankId());
        cashOutLog.setCashOutStatus(CashOutStatus.PENDING);
        
        cashOutRepository.save(cashOutLog);
        auditLogService.saveAudit( null, objectMapper.writeValueAsString(cashOutLog), Module.CASH_OUT, servletRequest,"Customer logged cash out request", cashOutLog.getId());
        return CashOutResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Cashout successfully logged");
    
    }
    
    
    private CashOutValidationResponse validateRequest(CashOutRequest cashOutRequest) {
        
        String errorMessage = "";
        
        Wallet wallet;

        Bank bank = bankRepository.getOne(cashOutRequest.getBankId());
        if (bank== null) {
            return new CashOutValidationResponse("Invalid Bank", null);
        }
        
        if (cashOutRequest == null) return new CashOutValidationResponse("Request cannot be null", null);

        if (cashOutRequest.getAmount() == null || cashOutRequest.getAmount().compareTo(BigDecimal.valueOf(0d)) <= 0) return new CashOutValidationResponse("Amount must be greater than 0", null);

        if (cashOutRequest.getWalletId() == null) return new CashOutValidationResponse("WalletId is required", null);

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null || userDetailsTokenEnvelope.getManagedUser().getId() == null) {
            return new CashOutValidationResponse("No logged in user found", null);
        }

        wallet = walletRepository.findOne(cashOutRequest.getWalletId());

        if (wallet== null || wallet.getManagedUser() == null || !wallet.getManagedUser().getEmail().equalsIgnoreCase(userDetailsTokenEnvelope.getManagedUser().getEmail())) {
            return new CashOutValidationResponse("Wallet not found for user", null);
        }

        if (wallet.getAvailableAccountBalance().compareTo(cashOutRequest.getAmount()) < 0) return new CashOutValidationResponse("Insufficient available balance in wallet", null);
        
        return new CashOutValidationResponse(errorMessage, wallet);

    }
    
    public CashOutResponse searchCashOutWithOr(CashOutRequest cashOutRequest) {

        if (cashOutRequest == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "request object cannot be null");

        Integer pageNo = cashOutRequest.getPageNo() == null || cashOutRequest.getPageNo() < 0 ? 0 : cashOutRequest.getPageNo();
        Integer pageSize = cashOutRequest.getPageSize() == null || cashOutRequest.getPageSize() < 1 ? 15 : cashOutRequest.getPageSize();

        Page cashOutPage = cashOutRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.or(getPredicateListForSearch(cashOutRequest, criteriaBuilder, root).toArray(new Predicate[0]));

        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        CashOutResponse cashOutResponse = new CashOutResponse();
        cashOutResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        cashOutResponse.setCashOutLogPage(cashOutPage);

        return cashOutResponse;

    }



    public CashOutResponse searchCashOutByPage(Integer pageNo, Integer pageSize) {

        pageNo = (pageNo == null || pageNo < 0) ? 0 : pageNo;
        pageSize = (pageSize == null || pageSize < 1) ? 15 : pageSize;

        Page<CashOutLog> cashOutLogPage = cashOutRepository.findAllByIdNotNull(new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        CashOutResponse cashOutResponse = new CashOutResponse();
        cashOutResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        cashOutResponse.setCashOutLogPage(cashOutLogPage);


        return cashOutResponse;
    }

    public CashOutResponse searchCashOutWithAnd(CashOutRequest cashOutRequest) {

        if (cashOutRequest == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "request object cannot be null");

        Integer pageNo = cashOutRequest.getPageNo() == null || cashOutRequest.getPageNo() < 0 ? 0 : cashOutRequest.getPageNo();
        Integer pageSize = cashOutRequest.getPageSize() == null || cashOutRequest.getPageSize() < 1 ? 15 : cashOutRequest.getPageSize();

        Page cashOutPage = cashOutRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.and(getPredicateListForSearch(cashOutRequest, criteriaBuilder, root).toArray(new Predicate[0]));

        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        CashOutResponse cashOutResponse = new CashOutResponse();
        cashOutResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        cashOutResponse.setCashOutLogPage(cashOutPage);

        return cashOutResponse;

    }

    public CashOutResponse searchCashOutWithAnd(Pageable pageable, CashOutRequest cashOutRequest) {

        if (cashOutRequest == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "request object cannot be null");

        Page cashOutPage = cashOutRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.and(getPredicateListForSearch(cashOutRequest, criteriaBuilder, root).toArray(new Predicate[0]));

        }, new PageRequest(pageable.getOffset()/pageable.getPageSize(), pageable.getPageSize(), Sort.Direction.DESC, "id"));

        CashOutResponse cashOutResponse = new CashOutResponse();
        cashOutResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        cashOutResponse.setCashOutLogPage(cashOutPage);

        return cashOutResponse;

    }

    public List<Predicate> getPredicateListForSearch(CashOutRequest cashOutRequest, CriteriaBuilder criteriaBuilder, Root root) {
        List<Predicate> searchPredicate = new ArrayList<>();

        if (!StringUtils.isEmpty(cashOutRequest.getCurrencyType()))
            searchPredicate.add(criteriaBuilder.equal((root.get("wallet").get("currency").get("type")), cashOutRequest.getCurrencyType()));
        if (!StringUtils.isEmpty(cashOutRequest.getFirstName()))
            searchPredicate.add(criteriaBuilder.like((root.get("wallet").get("managedUser").get("firstName")), cashOutRequest.getFirstName()));
        if (!StringUtils.isEmpty(cashOutRequest.getLastName()))
            searchPredicate.add(criteriaBuilder.like((root.get("wallet").get("managedUser").get("lastName")), cashOutRequest.getLastName()));
        if (!StringUtils.isEmpty(cashOutRequest.getEmail()))
            searchPredicate.add(criteriaBuilder.like((root.get("wallet").get("managedUser").get("email")), cashOutRequest.getEmail()));
        if (cashOutRequest.getCashOutLogId() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("id")), cashOutRequest.getCashOutLogId()));
        if (cashOutRequest.getCashOutStatus() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("cashOutStatus")), cashOutRequest.getCashOutStatus()));
        if (cashOutRequest.getWalletId() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("wallet")), cashOutRequest.getWalletId()));
        if (cashOutRequest.getFromDate() != null && cashOutRequest.getToDate() != null)
            searchPredicate.add(criteriaBuilder.between((root.get("requestDate")), cashOutRequest.getFromDate(), cashOutRequest.getToDate()));

        return searchPredicate;
    }

    public CashOutResponse approveCashOutForCustomer(CashOutRequest cashOutRequest, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (cashOutRequest == null || cashOutRequest.getCashOutLogId() == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Cash out Id cannot be null");

        if (StringUtils.isEmpty(cashOutRequest.getNarration())) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Narration is required for approval");

        ManagedUser currentUser = userService.getCurrentLoggedInUser();

        if (currentUser == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Current user not found");

        CashOutLog cashOutLog = cashOutRepository.findCashOutLogByIdAndCashOutStatus(cashOutRequest.getCashOutLogId(), CashOutStatus.PENDING);

        if (cashOutLog == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Cash out record not found for approval");

        CashOutLog oldreference = cashOutLog;


        Wallet wallet = cashOutLog.getWallet();

        if (wallet == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Wallet not found");
        BigDecimal newLedgerBalance = wallet.getLedgerAccountBalance().subtract(cashOutLog.getAmount());
        wallet.setLedgerAccountBalance(newLedgerBalance);
        walletRepository.save(wallet);

        cashOutLog.setAdminNarration(cashOutRequest.getNarration());
        cashOutLog.setVerifiedDate(new Date());
        cashOutLog.setVerifiedBy(currentUser);
        cashOutLog.setWallet(wallet);
        cashOutLog.setCashOutStatus(CashOutStatus.VERIFIED);

        cashOutRepository.save(cashOutLog);

        auditLogService.saveAudit(objectMapper.writeValueAsString(oldreference), objectMapper.writeValueAsString(cashOutLog), Module.CASH_OUT, servletRequest,"Admin user verified cash out request", cashOutLog.getId());


        return CashOutResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Cashout successfully verified");

    }

    public CashOutResponse declineCashOutForCustomer(CashOutRequest cashOutRequest, HttpServletRequest servletRequest) throws JsonProcessingException {

        if (cashOutRequest == null || cashOutRequest.getCashOutLogId() == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Cash out Id cannot be null");

        if (StringUtils.isEmpty(cashOutRequest.getNarration())) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Narration is required for declining");

        ManagedUser currentUser = userService.getCurrentLoggedInUser();

        if (currentUser == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Current user not found");

        CashOutLog cashOutLog = cashOutRepository.findCashOutLogByIdAndCashOutStatus(cashOutRequest.getCashOutLogId(), CashOutStatus.PENDING);

        if (cashOutLog == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Cash out record not found for approval");

        CashOutLog oldreference = cashOutLog;

        Wallet wallet = cashOutLog.getWallet();

        if (wallet == null) return CashOutResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Wallet not found");

        BigDecimal newAvailableBalance = wallet.getAvailableAccountBalance().add(cashOutLog.getAmount());
        wallet.setAvailableAccountBalance(newAvailableBalance);
        walletRepository.save(wallet);

        cashOutLog.setAdminNarration(cashOutRequest.getNarration());
        cashOutLog.setVerifiedDate(new Date());
        cashOutLog.setVerifiedBy(currentUser);
        cashOutLog.setCashOutStatus(CashOutStatus.DECLINED);
        cashOutLog.setWallet(wallet);
        cashOutRepository.save(cashOutLog);

        auditLogService.saveAudit(objectMapper.writeValueAsString(oldreference), objectMapper.writeValueAsString(cashOutLog), Module.CASH_OUT, servletRequest,"Admin user declined cash out request", cashOutLog.getId());


        return CashOutResponse.fromCodeAndNarration(ApiResponseCode.SUCCESSFUL, "Cashout successfully declined");

    }


}
