package com.plethub.paaro.core.appservice.services;

import com.plethub.paaro.core.appservice.apirequestmodel.TransferTransactionSearchRequestModel;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletTransferRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.*;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.appservice.enums.WalletStatus;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
import com.plethub.paaro.core.models.CurrencyDetails;
import com.plethub.paaro.core.models.CustomerStat;
import com.plethub.paaro.core.models.TransferTransactionType;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import com.plethub.paaro.core.appservice.repository.CurrencyDetailsRepository;
import com.plethub.paaro.core.appservice.repository.WalletTransferTransactionRepository;
import com.plethub.paaro.core.infrastructure.utils.DateUtils;
import com.plethub.paaro.core.usermanagement.enums.Module;
//import com.plethub.paaro.core.security.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.plethub.paaro.core.infrastructure.utils.GeneralUtil.getUnmatchedStatuses;

@Service
public class TransactionSearchService {

    @Autowired
    private WalletTransferTransactionRepository walletTransferTransactionRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private CurrencyDetailsRepository currencyDetailsRepository;

    @Value("${currency.naira-type:NGN}")
    private String nairaCurrency;

    @Value("${currency.naira-type:GBP}")
    private String gbpCurrency;

    private Logger logger = LoggerFactory.getLogger(TransactionSearchService.class.getName());

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByUserWallet(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getCurrency())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "currency cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }

        List<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndFromCurrency_Type(email, walletTransferRequest.getCurrency());

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by currency");
        walletTransferRequestResponse.setWalletTransferTransactions(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByUserWalletPaged(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getCurrency())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "currency cannot be blank");
        }

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }

        Pageable pageable = new PageRequest(walletTransferRequest.getPageNo(), walletTransferRequest.getPageSize());

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndFromCurrency_Type(email, walletTransferRequest.getCurrency(), pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by currency");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByUserWalletAndEmail(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getCurrency()) || StringUtils.isEmpty(walletTransferRequest.getEmail())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "currency and email cannot be blank");
        }

        List<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndFromCurrency_Type(walletTransferRequest.getEmail(), walletTransferRequest.getCurrency());

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by currency and email");
        walletTransferRequestResponse.setWalletTransferTransactions(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByUserWalletAndEmailPaged(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getCurrency()) || StringUtils.isEmpty(walletTransferRequest.getEmail())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "currency and email cannot be blank");
        }

        Pageable pageable = new PageRequest(walletTransferRequest.getPageNo(), walletTransferRequest.getPageSize());

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndFromCurrency_Type(walletTransferRequest.getEmail(), walletTransferRequest.getCurrency(), pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by currency and email");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }


    public WalletTransferRequestResponse findAllTransferWalletTransactionsByEmail(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getEmail())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email cannot be blank");
        }

        List<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_Email(walletTransferRequest.getEmail());

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by email");
        walletTransferRequestResponse.setWalletTransferTransactions(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByEmailPaged(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getEmail())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email cannot be blank");
        }

        Pageable pageable = new PageRequest(walletTransferRequest.getPageNo(), walletTransferRequest.getPageSize(), Sort.Direction.DESC, "id" );

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_Email(walletTransferRequest.getEmail(), pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllTransferWalletTransactionsByEmailPagedWithFilter(WalletTransferRequest walletTransferRequest) {

        if (walletTransferRequest == null || StringUtils.isEmpty(walletTransferRequest.getEmail()) || StringUtils.isEmpty(walletTransferRequest.getFilter())) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Email and filter cannot be blank");
        }

        Pageable pageable = new PageRequest(walletTransferRequest.getPageNo(), walletTransferRequest.getPageSize(), Sort.Direction.DESC, "id" );

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndToAccountNameLike(walletTransferRequest.getEmail(), "%"+walletTransferRequest.getFilter().trim()+"%",pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllCustomerLoggedTransferWalletTransactions() {


        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }


        List<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndTransactionStatus(email, TransactionStatus.CUSTOMER_LOGGED_REQUEST);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Customer logged transactions fetched");
        walletTransferRequestResponse.setWalletTransferTransactions(transferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse findAllCustomerLoggedTransferWalletTransactionsPaged(int pageSize, int pageNo) {


        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetailsTokenEnvelope.getManagedUser().getEmail();

        if (StringUtils.isEmpty(email)) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.NULL_RESPONSE, "Unable to get email of the user");
        }
        Pageable pageable = new PageRequest(pageNo, pageSize);

        Page<WalletTransferTransaction> transferTransactions = walletTransferTransactionRepository.findAllByManagedUser_EmailAndTransactionStatus(email, TransactionStatus.CUSTOMER_LOGGED_REQUEST, pageable);

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Customer logged transactions fetched by page");
        walletTransferRequestResponse.setWalletTransferTransactionPage(transferTransactions);

        return walletTransferRequestResponse;

    }

    public Page<WalletTransferTransaction> findWalletTransferTransactionPage(int pageNo, int pageSize, String filter) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        return walletTransferTransactionRepository.findAllByIdIsNotNullAndToAccountNameIsLike("%"+filter+"%", pageRequest);

    }


    public Page<WalletTransferTransaction> findWalletTransferTransactionPageWithDateRange(int pageNo, int pageSize, String filter, String fromDateStr, String toDateStr) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        Date toDate = null;
        Date fromDate = null;

        try {
            toDate = dateUtils.convertStringToDate(toDateStr,"yyyy-MM-dd");
            fromDate = dateUtils.convertStringToDate(fromDateStr,"yyyy-MM-dd");
        } catch (ParseException e) {
            logger.error("Unable to convert date string to date due to ", e);
        }

        if (toDate == null || fromDate == null) {
            return new PageImpl<>(new ArrayList<>());
        }

        return walletTransferTransactionRepository.findAllByIdIsNotNullAndToAccountNameIsLikeAndInitiatedDateIsBetween("%"+filter+"%", fromDate, toDate, pageRequest);

    }

    public Page<WalletTransferTransaction> findWalletTransferTransactionPageWithFilter(int pageNo, int pageSize) {

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        return walletTransferTransactionRepository.findAllByIdIsNotNull(pageRequest);

    }

    public List<WalletTransferTransaction> findWalletTransferTransactionWithDateRange(String filter, String fromDateStr, String toDateStr) {


        Date toDate = null;
        Date fromDate = null;

        try {
            toDate = dateUtils.convertStringToDate(toDateStr,"yyyy-MM-dd");
            fromDate = dateUtils.convertStringToDate(fromDateStr,"yyyy-MM-dd");
        } catch (ParseException e) {
            logger.error("Unable to convert date string to date due to ", e);
        }

        if (toDate == null || fromDate == null) {
            return walletTransferTransactionRepository.findAllByIdIsNotNullAndToAccountNameIsLike("%"+filter+"%");
        }

        return walletTransferTransactionRepository.findAllByIdIsNotNullAndToAccountNameIsLikeAndInitiatedDateIsBetween("%"+filter+"%", fromDate, toDate);

    }

    public List<WalletTransferTransaction> findWalletTransferTransaction(String filter) {

        return walletTransferTransactionRepository.findAllByIdIsNotNullAndToAccountNameIsLike("%"+filter+"%");

    }

    public WalletTransferRequestResponse fetchAllUnmatchedSellTransactionsByPage(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {

            if (walletTransferRequest == null || walletTransferRequest.getPageNo() < 0 || walletTransferRequest.getPageSize() < 1) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request. Page no must not be less than 0 an page size must be greater than 0");
            }

            int pageNo = walletTransferRequest.getPageNo();
            int pageSize = walletTransferRequest.getPageSize();


            Page<WalletTransferTransaction> completedWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByTransactionStatusInAndTransferTransactionTypeIs(getUnmatchedStatuses(), TransferTransactionType.SELLING, new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"id"));

            WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
            walletTransferRequestResponse.setWalletTransferTransactionPage(completedWalletTransferTransactions);

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching completed transactions due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching completed transactions for user");
        }
    }

    public WalletTransferRequestResponse suggestTransactionsForMatch(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {

            if (walletTransferRequest == null) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request.");
            }

            if (StringUtils.isEmpty(walletTransferRequest.getFromCurrencyType())) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Currency type must be selected");
            }

            if (walletTransferRequest.getFromAmount() == null || walletTransferRequest.getFromAmount().compareTo(BigDecimal.valueOf(0)) < 0) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From amount must be greater than 0");
            }

            if (walletTransferRequest.getToAmount() == null || walletTransferRequest.getToAmount().compareTo(BigDecimal.valueOf(0)) < 0) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To amount must be greater than 0");
            }

            int pageNo = walletTransferRequest.getPageNo();
            int pageSize = walletTransferRequest.getPageSize();


            Page<WalletTransferTransaction> ongoingWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByTransactionStatusInAndActualAmountGreaterThanEqualAndActualAmountLessThanEqualAndFromCurrency_Type(getUnmatchedStatuses(), walletTransferRequest.getFromAmount(), walletTransferRequest.getToAmount(), walletTransferRequest.getFromCurrencyType(), new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"id"));

            WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
            walletTransferRequestResponse.setWalletTransferTransactionPage(ongoingWalletTransferTransactions);

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching ongoing transactions with amount and currency type due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions with amount and currency code for user");
        }
    }


    public WalletTransferRequestResponse findAllUserWalletTransferTransactionsPaged(WalletTransferRequest walletTransferRequest) {

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long id = 0L;

        if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
            id = userDetailsTokenEnvelope.getManagedUser().getId();
        }

        if (id == null) {
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "User not found. Please login again");
        }

        Page<WalletTransferTransaction> pagedTransferTransactions = walletTransferTransactionRepository.findAllByManagedUser_Id(id, new PageRequest(walletTransferRequest.getPageNo(), walletTransferRequest.getPageSize(), Sort.Direction.DESC, "id"));

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);
        walletTransferRequestResponse.setMessage("Transactions fetched by user details");
        walletTransferRequestResponse.setWalletTransferTransactionPage(pagedTransferTransactions);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse fetchOngoingTransactionDetailsByPage(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {

            int pageNo = walletTransferRequest.getPageNo();
            int pageSize = walletTransferRequest.getPageSize();

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long id = 0L;

            if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
                id = userDetailsTokenEnvelope.getManagedUser().getId();
            }

            Page<WalletTransferTransaction> ongoingWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByTransactionStatusNotInAndManagedUser_Id(Collections.singletonList(TransactionStatus.COMPLETED),id,new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"id"));

            WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
            walletTransferRequestResponse.setWalletTransferTransactionPage(ongoingWalletTransferTransactions);

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching ongoing transactions due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions for user");
        }
    }

    public CustomerStat fetchOngoingTransactionDetailsByPage(Long id) {

        try {

            List<TransactionStatus> ongoingStatus = new ArrayList<>();
            ongoingStatus.add(TransactionStatus.CUSTOMER_LOGGED_REQUEST);
            ongoingStatus.add(TransactionStatus.SYSTEM_MAPPED_REQUEST);
            ongoingStatus.add(TransactionStatus.CUSTOMER_MAPPED_REQUEST);
            ongoingStatus.add(TransactionStatus.INITIATOR_LOGGED_REQUEST);
            ongoingStatus.add(TransactionStatus.ADMIN_VERIFIED_LOGGED_REQUEST);
            ongoingStatus.add(TransactionStatus.PENDING);
            ongoingStatus.add(TransactionStatus.AWAITING_BANK_PAYMENT_VERIFICATION);

            Long completedId = walletTransferTransactionRepository.countAllByTransactionStatusInAndManagedUser_Id(Collections.singletonList(TransactionStatus.COMPLETED),id);
            Long ongoingId = walletTransferTransactionRepository.countAllByTransactionStatusInAndManagedUser_Id(ongoingStatus,id);
            Long allId = walletTransferTransactionRepository.countAllByTransactionStatusInAndManagedUser_Id(TransactionStatus.getTransactionStatus(),id);

            return new CustomerStat(completedId, ongoingId, allId);

        } catch (Exception e) {
            logger.error("Error occurred while fetching ongoing transactions due to ", e);
            //return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions for user");
            return new CustomerStat();
        }

    }

    public WalletTransferRequestResponse fetchCompletedTransactionDetailsByPage(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {

            if (walletTransferRequest == null || walletTransferRequest.getPageNo() < 0 || walletTransferRequest.getPageSize() < 1) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request. Page no must not be less than 0 an page size must be greater than 0");
            }

            int pageNo = walletTransferRequest.getPageNo();
            int pageSize = walletTransferRequest.getPageSize();

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long id = 0L;

            if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
                id = userDetailsTokenEnvelope.getManagedUser().getId();
            }

            Page<WalletTransferTransaction> completedWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByTransactionStatusInAndManagedUser_Id(Collections.singletonList(TransactionStatus.COMPLETED),id,new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"id"));

            WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
            walletTransferRequestResponse.setWalletTransferTransactionPage(completedWalletTransferTransactions);

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching completed transactions due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching completed transactions for user");
        }
    }


    public WalletTransferRequestResponse fetchAllUnmatchedBuyTransactionsByPage(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {

            if (walletTransferRequest == null || walletTransferRequest.getPageNo() < 0 || walletTransferRequest.getPageSize() < 1) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request. Page no must not be less than 0 an page size must be greater than 0");
            }

            int pageNo = walletTransferRequest.getPageNo();
            int pageSize = walletTransferRequest.getPageSize();


            Page<WalletTransferTransaction> completedWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByTransactionStatusInAndTransferTransactionTypeIs(getUnmatchedStatuses(), TransferTransactionType.BUYING, new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"id"));

            WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
            walletTransferRequestResponse.setWalletTransferTransactionPage(completedWalletTransferTransactions);

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching completed transactions due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching completed transactions for user");
        }
    }


    public WalletTransferRequestResponse fetchFundedTransactionDetailsByPage(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {

            int pageNo = walletTransferRequest.getPageNo();
            int pageSize = walletTransferRequest.getPageSize();

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long id = 0L;

            if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
                id = userDetailsTokenEnvelope.getManagedUser().getId();
            }

            Page<WalletTransferTransaction> ongoingWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByWalletStatusAndManagedUser_Id(WalletStatus.FUNDED,id,new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"id"));

            WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
            walletTransferRequestResponse.setWalletTransferTransactionPage(ongoingWalletTransferTransactions);

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching funded transactions due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching funded transactions for user");
        }
    }


    public WalletTransferRequestResponse fetchUnFundedTransactionDetailsByPage(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {

            int pageNo = walletTransferRequest.getPageNo();
            int pageSize = walletTransferRequest.getPageSize();

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long id = 0L;

            if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
                id = userDetailsTokenEnvelope.getManagedUser().getId();
            }

            Page<WalletTransferTransaction> ongoingWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByWalletStatusAndManagedUser_Id(WalletStatus.NOT_FUNDED,id,new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"id"));

            WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
            walletTransferRequestResponse.setWalletTransferTransactionPage(ongoingWalletTransferTransactions);

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching unfunded transactions due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching unfunded transactions for user");
        }
    }

    public WalletTransferRequestResponse suggestTransactionsByUserRateAndAmount(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {

            if (walletTransferRequest == null) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request.");
            }

            if (StringUtils.isEmpty(walletTransferRequest.getFromCurrencyType())) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From Currency type must be selected");
            }

            if (StringUtils.isEmpty(walletTransferRequest.getToCurrencyType())) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To Currency type must be selected");
            }

            if (walletTransferRequest.getActualAmount() == null || walletTransferRequest.getActualAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Actual amount must be greater than 0");
            }

            Double rate = walletTransferRequest.getExchangeRate();

            if ( rate == null || rate.compareTo(Double.valueOf(0)) <= 0 ) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Exchange rate must be greater than 0");
            }

            Double rateDeviation = getRateFromCurrencyDetails(walletTransferRequest.getToCurrencyType());

            if (rateDeviation == null) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.UNABLE_TO_PROCESS, "Rate not found for currency");
            }

            int pageNo = walletTransferRequest.getPageNo();
            int pageSize = walletTransferRequest.getPageSize();

            BigDecimal receiverEquivalent;

            if (walletTransferRequest.getFromCurrencyType().trim().equalsIgnoreCase(nairaCurrency)) {
                receiverEquivalent = walletTransferRequest.getActualAmount().divide(BigDecimal.valueOf(walletTransferRequest.getExchangeRate()), 2, RoundingMode.HALF_UP);
            } else {
                receiverEquivalent = walletTransferRequest.getActualAmount().multiply(BigDecimal.valueOf(walletTransferRequest.getExchangeRate())).setScale(2, RoundingMode.HALF_UP);
            }


            Page<WalletTransferTransaction> ongoingWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByTransactionStatusInAndActualAmountAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRateBetween(getUnmatchedStatuses(), receiverEquivalent, walletTransferRequest.getFromCurrencyType(),walletTransferRequest.getToCurrencyType(), (rate - rateDeviation), (rate + rateDeviation), new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"id"));

            WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
            walletTransferRequestResponse.setWalletTransferTransactionPage(ongoingWalletTransferTransactions);

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching transactions logged with amount and rate deviation due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions logged with amount and rate deviation due to");
        }
    }

    public WalletTransferRequestResponse suggestTransactionsByExactUserRateAndAmount(WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest) {

        try {
            boolean closeMatch = false;
            if (walletTransferRequest == null) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request.");
            }

            if (StringUtils.isEmpty(walletTransferRequest.getFromCurrencyType())) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "From Currency type must be selected");
            }

            if (StringUtils.isEmpty(walletTransferRequest.getToCurrencyType())) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "To Currency type must be selected");
            }

            if (walletTransferRequest.getActualAmount() == null || walletTransferRequest.getActualAmount().compareTo(BigDecimal.valueOf(0)) <= 0) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Actual amount must be greater than 0");
            }

            Double rate = walletTransferRequest.getExchangeRate();

            if ( rate == null || rate.compareTo(Double.valueOf(0)) <= 0 ) {
                return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Exchange rate must be greater than 0");
            }

            int pageNo = walletTransferRequest.getPageNo();
            int pageSize = walletTransferRequest.getPageSize();

            BigDecimal receiverEquivalent;

            if (walletTransferRequest.getFromCurrencyType().trim().equalsIgnoreCase(nairaCurrency)) {
                receiverEquivalent = walletTransferRequest.getActualAmount().divide(BigDecimal.valueOf(walletTransferRequest.getExchangeRate()), 2, RoundingMode.HALF_UP);
            } else {
                receiverEquivalent = walletTransferRequest.getActualAmount().multiply(BigDecimal.valueOf(walletTransferRequest.getExchangeRate())).setScale(2, RoundingMode.HALF_UP);;
            }


            Page<WalletTransferTransaction> exactMatchWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByTransactionStatusInAndTotalAmountAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRate(getUnmatchedStatuses(), receiverEquivalent, walletTransferRequest.getFromCurrencyType(),walletTransferRequest.getToCurrencyType(), rate , new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"actualAmount"));

            if (CollectionUtils.isEmpty(exactMatchWalletTransferTransactions.getContent()))
                closeMatch = true;

            Pair<Double,Double> rateAndAmountDeviation = getRateAndAmountDeviationDetails(walletTransferRequest.getToCurrencyType());


            Page<WalletTransferTransaction> closeMatchWalletTransferTransactions =
                            walletTransferTransactionRepository.findAllByTransactionStatusInAndActualAmountBetweenAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRateBetween(getUnmatchedStatuses(), receiverEquivalent.subtract(BigDecimal.valueOf(rateAndAmountDeviation.getRight())), receiverEquivalent.add(BigDecimal.valueOf(rateAndAmountDeviation.getRight())), walletTransferRequest.getFromCurrencyType(),walletTransferRequest.getToCurrencyType(), (rate - rateAndAmountDeviation.getLeft()), (rate + rateAndAmountDeviation.getLeft()), new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"actualAmount"));


//            if (CollectionUtils.isEmpty(ongoingWalletTransferTransactions.getContent())) {
//
//                    ongoingWalletTransferTransactions =
//                            walletTransferTransactionRepository.findAllByTransactionStatusInAndTotalAmountAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRateBetween(getUnmatchedStatuses(), receiverEquivalent, walletTransferRequest.getFromCurrencyType(),walletTransferRequest.getToCurrencyType(), (rate - rateAndAmountDeviation.getLeft()), (rate + rateAndAmountDeviation.getLeft()), new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"actualAmount"));
//
//            }
//
//            if (CollectionUtils.isEmpty(ongoingWalletTransferTransactions.getContent())) {
//
//                    ongoingWalletTransferTransactions =
//                            walletTransferTransactionRepository.findAllByTransactionStatusInAndTotalAmountBetweenAndToCurrency_TypeAndFromCurrency_TypeAndExchangeRateBetween(getUnmatchedStatuses(), receiverEquivalent.subtract(BigDecimal.valueOf(rateAndAmountDeviation.getRight())), receiverEquivalent.add(BigDecimal.valueOf(rateAndAmountDeviation.getRight())), walletTransferRequest.getFromCurrencyType(),walletTransferRequest.getToCurrencyType(), (rate - rateAndAmountDeviation.getLeft()), (rate + rateAndAmountDeviation.getLeft()), new PageRequest(pageNo,pageSize, Sort.Direction.DESC,"actualAmount"));
//                    closeMatch = true;
//            }

            WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
            walletTransferRequestResponse.setExactMatchTransactionPage(exactMatchWalletTransferTransactions);
            walletTransferRequestResponse.setCloseMatchTransferTransactionPage(closeMatchWalletTransferTransactions);
            walletTransferRequestResponse.setCloseMatch(closeMatch);

            return walletTransferRequestResponse;
        } catch (Exception e) {
            logger.error("Error occurred while fetching transactions logged with amount and rate deviation due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions logged with amount and rate deviation due to");
        }
    }


    private Double getRateFromCurrencyDetails(String currencyType) {
        CurrencyDetails currencyDetails = currencyDetailsRepository.findByCurrencyCode(currencyType);
        if (currencyDetails == null) {
            return null;
        }

        return (currencyDetails.getRateSearchDeviation() == null) ? 0 : currencyDetails.getRateSearchDeviation();

    }

    private ImmutablePair<Double, Double> getRateAndAmountDeviationDetails(String currencyType) {
        CurrencyDetails currencyDetails = currencyDetailsRepository.findByCurrencyCode(currencyType);
        if (currencyDetails == null) {
            return new ImmutablePair<>(0d,0d);
        }

        Double rateSearchDeviation = (currencyDetails.getRateSearchDeviation() == null) ? 0d : currencyDetails.getRateSearchDeviation();
        Double amountSearchDeviation = (currencyDetails.getAmountSearchDeviation() == null) ? 0d : currencyDetails.getAmountSearchDeviation();

        return new ImmutablePair<>(rateSearchDeviation,amountSearchDeviation);

    }

    public WalletTransferRequestResponse searchTransactionsByOrOptions(TransferTransactionSearchRequestModel requestModel) {

        if (requestModel == null) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");


        Integer pageNo = requestModel.getPageNo() == null || requestModel.getPageNo() < 0 ? 0 : requestModel.getPageNo();
        Integer pageSize = requestModel.getPageSize() == null || requestModel.getPageSize() < 1 ? 15 : requestModel.getPageSize();


        Page transactions = walletTransferTransactionRepository.findAll((Specification<WalletTransferTransaction>) (root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.or(getSearchPredicateFromRequest(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));
        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setWalletTransferTransactionPage(transactions);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse searchTransactionsByAndOptions(TransferTransactionSearchRequestModel requestModel) {

        if (requestModel == null) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");

        Integer pageNo = requestModel.getPageNo() == null || requestModel.getPageNo() < 0 ? 0 : requestModel.getPageNo();
        Integer pageSize = requestModel.getPageSize() == null || requestModel.getPageSize() < 1 ? 15 : requestModel.getPageSize();

        Page transactions = walletTransferTransactionRepository.findAll((Specification<WalletTransferTransaction>) (root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.and(getSearchPredicateFromRequest(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));
        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setWalletTransferTransactionPage(transactions);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        return walletTransferRequestResponse;

    }

    public WalletTransferRequestResponse searchTransactionsByAndOptionsPageable(Pageable pageable, TransferTransactionSearchRequestModel requestModel) {

        if (requestModel == null) return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.INVALID_REQUEST, "Invalid request");


       Page transactions = walletTransferTransactionRepository.findAll((Specification<WalletTransferTransaction>) (root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.and(getSearchPredicateFromRequest(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));
        }, new PageRequest(pageable.getOffset()/pageable.getPageSize(), pageable.getPageSize(), Sort.Direction.DESC, "id"));

        WalletTransferRequestResponse walletTransferRequestResponse = new WalletTransferRequestResponse();
        walletTransferRequestResponse.setWalletTransferTransactionPage(transactions);
        walletTransferRequestResponse.setResponseStatus(ApiResponseCode.SUCCESSFUL);

        return walletTransferRequestResponse;

    }

    private List<Predicate> getSearchPredicateFromRequest(TransferTransactionSearchRequestModel requestModel, CriteriaBuilder criteriaBuilder, Root root) {
        List<Predicate> searchPredicate = new ArrayList<>();

        if (requestModel.getTransactionStatus() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("transactionStatus")), requestModel.getTransactionStatus()));
        if (requestModel.getId() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("id")), requestModel.getId()));
        if (!StringUtils.isEmpty(requestModel.getAccountName()))
            searchPredicate.add(criteriaBuilder.like((root.get("toAccountName")), "%"+requestModel.getAccountName()+"%"));
        if (!StringUtils.isEmpty(requestModel.getAccountNumber()))
            searchPredicate.add(criteriaBuilder.like((root.get("toAccountNumber")), "%"+requestModel.getAccountNumber()+"%"));
        if (!StringUtils.isEmpty(requestModel.getPaaroReferenceId()))
            searchPredicate.add(criteriaBuilder.like((root.get("paaroReferenceId")), "%"+requestModel.getPaaroReferenceId()+"%"));
        if (!StringUtils.isEmpty(requestModel.getFromCurrency()))
            searchPredicate.add(criteriaBuilder.like((root.get("fromCurrency").get("type")), "%"+requestModel.getFromCurrency()+"%"));
        if (!StringUtils.isEmpty(requestModel.getToCurrency()))
            searchPredicate.add(criteriaBuilder.like((root.get("toCurrency").get("type")), "%"+requestModel.getToCurrency()+"%"));
        if (requestModel.getUserId() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("managedUser").get("id")), requestModel.getUserId()));
        if (requestModel.getSettled() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("isSettled")), requestModel.getSettled()));
        if (requestModel.getMapped() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("mapped")), requestModel.getMapped()));
        if (requestModel.getTotalAmount() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("totalAmount")), requestModel.getTotalAmount()));
        if (!CollectionUtils.isEmpty(requestModel.getStatuses()))
            searchPredicate.add((root.get("transactionStatus").in(requestModel.getStatuses())));
        if (requestModel.getFromDate() != null && requestModel.getToDate() != null)
            searchPredicate.add(criteriaBuilder.between((root.get("initiatedDate")), requestModel.getFromDate(), requestModel.getToDate()));

        return searchPredicate;

    }

        public TransferDashboardDetails getDashboardDetailsForTransactions() {
        try {
            Date currentDate = new Date();
            TransferDashboardDetails transferDashboardDetails = new TransferDashboardDetails();
            transferDashboardDetails.setDailyTransfersCount(NumberFormat.getInstance().format(walletTransferTransactionRepository.getDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentDay(), currentDate)));
            transferDashboardDetails.setDailyMatchedTransfersCount(NumberFormat.getInstance().format(walletTransferTransactionRepository.getMatchedTransactionDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentDay(), currentDate)));

            transferDashboardDetails.setWeeklyTransfersCount(NumberFormat.getInstance().format(walletTransferTransactionRepository.getDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentWeek(), currentDate)));
            transferDashboardDetails.setWeeklyMatchedTransfersCount(NumberFormat.getInstance().format(walletTransferTransactionRepository.getMatchedTransactionDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentWeek(), currentDate)));

            transferDashboardDetails.setMonthlyTransfersCount(NumberFormat.getInstance().format(walletTransferTransactionRepository.getDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentMonth(), currentDate)));
            transferDashboardDetails.setMonthlyMatchedTransfersCount(NumberFormat.getInstance().format(walletTransferTransactionRepository.getMatchedTransactionDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentMonth(), currentDate)));

            transferDashboardDetails.setYearlyTransfersCount(NumberFormat.getInstance().format(walletTransferTransactionRepository.getDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentYear(), currentDate)));
            transferDashboardDetails.setYearlyMatchedTransfersCount(NumberFormat.getInstance().format(walletTransferTransactionRepository.getMatchedTransactionDashboardDetailsWithdateRange(GeneralUtil.getStartOfCurrentYear(), currentDate)));

            transferDashboardDetails.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            return transferDashboardDetails;
        } catch (Exception e) {
            logger.error("System error occurred while fetching transfer dashboard details due to ", e);
            return TransferDashboardDetails.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occcurred while fetching transfer dashboard details");
        }
    }

    public RevenueDashboardDetails getDashboardDetailsForRevenue() {
        try {
            Date currentDate = new Date();
            RevenueDashboardDetails revenueDashboardDetails = new RevenueDashboardDetails();
            revenueDashboardDetails.setDailyForeignRevenue(formatWithComma(walletTransferTransactionRepository.getDashboardDetailsWithDateRangeForRevenue(GeneralUtil.getStartOfCurrentDay(), currentDate, gbpCurrency)));
            revenueDashboardDetails.setDailyNairaRevenue(formatWithComma(walletTransferTransactionRepository.getDashboardDetailsWithDateRangeForRevenue(GeneralUtil.getStartOfCurrentDay(), currentDate, nairaCurrency)));
            revenueDashboardDetails.setWeeklyForeignRevenue(formatWithComma(walletTransferTransactionRepository.getDashboardDetailsWithDateRangeForRevenue(GeneralUtil.getStartOfCurrentWeek(), currentDate, gbpCurrency)));
            revenueDashboardDetails.setWeeklyNairaRevenue(formatWithComma(walletTransferTransactionRepository.getDashboardDetailsWithDateRangeForRevenue(GeneralUtil.getStartOfCurrentWeek(), currentDate, nairaCurrency)));
            revenueDashboardDetails.setMonthlyForeignRevenue(formatWithComma(walletTransferTransactionRepository.getDashboardDetailsWithDateRangeForRevenue(GeneralUtil.getStartOfCurrentMonth(), currentDate, gbpCurrency)));
            revenueDashboardDetails.setMonthlyNairaRevenue(formatWithComma(walletTransferTransactionRepository.getDashboardDetailsWithDateRangeForRevenue(GeneralUtil.getStartOfCurrentMonth(), currentDate, nairaCurrency)));
            revenueDashboardDetails.setYearlyForeignRevenue(formatWithComma(walletTransferTransactionRepository.getDashboardDetailsWithDateRangeForRevenue(GeneralUtil.getStartOfCurrentYear(), currentDate, gbpCurrency)));
            revenueDashboardDetails.setYearlyNairaRevenue(formatWithComma(walletTransferTransactionRepository.getDashboardDetailsWithDateRangeForRevenue(GeneralUtil.getStartOfCurrentYear(), currentDate, nairaCurrency)));

            revenueDashboardDetails.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            return revenueDashboardDetails;
        } catch (Exception e) {
            logger.error("System error occurred while fetching charges dashboard details due to ", e);
            return RevenueDashboardDetails.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occcurred while fetching charges dashboard details");
        }
    }


    private String formatWithComma(BigDecimal bigDecimal){
        if (null != bigDecimal){
            return NumberFormat.getInstance().format(bigDecimal);
        }else{
            return NumberFormat.getInstance().format(new BigDecimal(0));
        }
    }


    public BidDashboardDetails getDashboardDetailsForBid() {
        try {
            Date currentDate = new Date();
            BidDashboardDetails bidDashboardDetails = new BidDashboardDetails();
            List<TransactionStatus> transactionStatuses = new ArrayList<>();
            transactionStatuses.add(TransactionStatus.COMPLETED);
            transactionStatuses.add(TransactionStatus.CUSTOMER_MAPPED_REQUEST);
            transactionStatuses.add(TransactionStatus.SYSTEM_MAPPED_REQUEST);

            bidDashboardDetails.setDailyExistingUsers(walletTransferTransactionRepository.getBidDetailsWithDateRangeForExistingUser(GeneralUtil.getStartOfCurrentDay(), currentDate, transactionStatuses));
            bidDashboardDetails.setDailyNewUsers(walletTransferTransactionRepository.getBidDetailsWithDateRangeForNewUser(GeneralUtil.getStartOfCurrentDay(), currentDate, transactionStatuses));

            bidDashboardDetails.setWeeklyExistingUsers(walletTransferTransactionRepository.getBidDetailsWithDateRangeForExistingUser(GeneralUtil.getStartOfCurrentWeek(), currentDate, transactionStatuses));
            bidDashboardDetails.setWeeklyNewUsers(walletTransferTransactionRepository.getBidDetailsWithDateRangeForNewUser(GeneralUtil.getStartOfCurrentWeek(), currentDate, transactionStatuses));

            bidDashboardDetails.setMonthlyExistingUsers(walletTransferTransactionRepository.getBidDetailsWithDateRangeForExistingUser(GeneralUtil.getStartOfCurrentMonth(), currentDate, transactionStatuses));
            bidDashboardDetails.setMonthlyNewUsers(walletTransferTransactionRepository.getBidDetailsWithDateRangeForNewUser(GeneralUtil.getStartOfCurrentMonth(), currentDate, transactionStatuses));

            bidDashboardDetails.setYearlyExistingUsers(walletTransferTransactionRepository.getBidDetailsWithDateRangeForExistingUser(GeneralUtil.getStartOfCurrentYear(), currentDate, transactionStatuses));
            bidDashboardDetails.setYearlyNewUsers(walletTransferTransactionRepository.getBidDetailsWithDateRangeForNewUser(GeneralUtil.getStartOfCurrentYear(), currentDate, transactionStatuses));

            bidDashboardDetails.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
            return bidDashboardDetails;
        } catch (Exception e) {
            logger.error("System error occurred while fetching charges dashboard details due to ", e);
            return BidDashboardDetails.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occcurred while fetching charges dashboard details");
        }
    }






}
