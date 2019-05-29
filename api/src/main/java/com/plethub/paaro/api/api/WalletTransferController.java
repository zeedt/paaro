package com.plethub.paaro.api.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plethub.paaro.core.appservice.apirequestmodel.TransferTransactionSearchRequestModel;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletTransferRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.BidDashboardDetails;
import com.plethub.paaro.core.appservice.apiresponsemodel.RevenueDashboardDetails;
import com.plethub.paaro.core.appservice.apiresponsemodel.TransferDashboardDetails;
import com.plethub.paaro.core.appservice.apiresponsemodel.WalletTransferRequestResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.services.TransactionDownloadService;
import com.plethub.paaro.core.appservice.services.TransactionSearchService;
import com.plethub.paaro.core.appservice.services.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/wallet")
public class WalletTransferController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransactionSearchService transactionSearchService;

    @Autowired
    private TransactionDownloadService transactionDownloadService;


    private Logger logger = LoggerFactory.getLogger(WalletTransferController.class.getName());

    @RequestMapping(value = "transfer_to_account", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse createCustomerTransferRequest(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transferService.createCustomerTransferRequest(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while logging transfer request ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while logging transfer request");
        }

    }

    @RequestMapping(value = "find_wallet_transfer_transactions", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse findAllLoggedInUserWalletTransferTransactions(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transactionSearchService.findAllTransferWalletTransactionsByUserWallet(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet transfer transactions for logged in user ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet transfer transaction by email and currency for logged in user");
        }

    }
    @RequestMapping(value = "find_wallet_transfer_transactions/paged", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse findAllLoggedInUserWalletTransferTransactionsPaged(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transactionSearchService.findAllTransferWalletTransactionsByUserWalletPaged(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet transfer transactions for logged in user ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet transfer transaction by email and currency for logged in user");
        }

    }

    @PreAuthorize(value = "hasAnyAuthority('FETCH_TRANSFER_TRANSACTIONS_BY_EMAIL_AND_CURRENCY')")
    @RequestMapping(value = "find_wallet_transfer_transactions_by_email_and_currency", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse findAllWalletTransferTransactionsByEmailAndCurrency(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transactionSearchService.findAllTransferWalletTransactionsByUserWalletAndEmail(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet transfer transactions by email and currency ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet transfer transactions by email and currency");
        }

    }
    @PreAuthorize(value = "hasAnyAuthority('FETCH_TRANSFER_TRANSACTIONS_BY_EMAIL_AND_CURRENCY')")
    @RequestMapping(value = "find_wallet_transfer_transactions_by_email_and_currency/paged", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse findAllWalletTransferTransactionsByEmailAndCurrencyPaged(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transactionSearchService.findAllTransferWalletTransactionsByUserWalletAndEmailPaged(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet transfer transactions by email and currency ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet transfer transactions by email and currency");
        }

    }

    @PreAuthorize(value = "hasAnyAuthority('FETCH_TRANSFER_TRANSACTIONS_BY_EMAIL')")
    @RequestMapping(value = "find_wallet_transfer_transactions_by_email", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse findAllWalletTransferTransactionsByEmail(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transactionSearchService.findAllTransferWalletTransactionsByEmail(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet transfer transactions by email ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet transfer transactions for logged in user by email");
        }

    }
    @PreAuthorize(value = "hasAnyAuthority('FETCH_TRANSFER_TRANSACTIONS_BY_EMAIL')")
    @RequestMapping(value = "find_wallet_transfer_transactions_by_email/paged", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse findAllWalletTransferTransactionsByEmailPaged(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transactionSearchService.findAllTransferWalletTransactionsByEmailPaged(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet transfer transactions by email with page ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet transfer transactions for logged in user by email");
        }

    }

    @RequestMapping(value = "find_all_unsettled_transactions_for_logged_in_user/customer_logged", method = RequestMethod.GET)
    @ResponseBody
    public WalletTransferRequestResponse findAllUnsettledTransactionsForLoggedInUser()  {

        try {
            return transactionSearchService.findAllCustomerLoggedTransferWalletTransactions();
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet transfer transactions ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching all unsettled wallet transfer transactions of logged in user");
        }

    }
    @RequestMapping(value = "find_all_unsettled_transactions_for_logged_in_user/customer_logged/paged", method = RequestMethod.GET)
    @ResponseBody
    public WalletTransferRequestResponse findAllUnsettledTransactionsForLoggedInUserPaged(@RequestParam("pageSize") int pageSize, @RequestParam("pageNo") int pageNo)  {

        try {
            return transactionSearchService.findAllCustomerLoggedTransferWalletTransactionsPaged(pageSize, pageNo);
        } catch (Exception e) {
            logger.error("Error occurred while fetching all unsettled transfer transactions for logged in user ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching unsettled wallet transfer transaction of logged in user paged");
        }

    }

    @RequestMapping(value = "compute-charges-and-values", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse computeChargesAndValuesForWallet(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transferService.computeChargesAndSendToken(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while computing wallet transfer transactions charge and values ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while computing charges and values");
        }

    }

    @RequestMapping(value = "compute-charges-and-values-before-initiation", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse computeChargesAndValuesBeforeLoggingRequest(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transferService.computeChargesAndValuesBeforeLoggingRequest(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while computing wallet transfer transactions charge and values ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while computing charges and values");
        }

    }


    @RequestMapping(value = "initiateTransferRequestForUser", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse initiateTransferRequestForUser(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transferService.initiateTransferRequestForUser(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while initiating wallet transfer transactions for user by admin user", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet transfer transaction by currency");
        }

    }


    @RequestMapping(value = "ongoing-transactions", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse fetchOngoingTransactionsForUser(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transactionSearchService.fetchOngoingTransactionDetailsByPage(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while initiating wallet transfer transactions for user by admin user", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions for user");
        }

    }


    @RequestMapping(value = "completed-transactions", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse fetchCompletedTransactionsForUser(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transactionSearchService.fetchCompletedTransactionDetailsByPage(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while initiating wallet transfer transactions for user by admin user", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions for user");
        }

    }



    @RequestMapping(value = "unmatched-buy-transactions", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse fetchAllUnmatchedBuyTransactions(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transactionSearchService.fetchAllUnmatchedBuyTransactionsByPage(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching unmatched buy transactions", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching unmatched buy transactions");
        }

    }

    @RequestMapping(value = "unmatched-sell-transactions", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse fetchAllUnmatchedSellTransactions(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transactionSearchService.fetchAllUnmatchedSellTransactionsByPage(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching unmatched sell transactions", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching unmatched sell transactions");
        }

    }

    @RequestMapping(value = "suggest-unmatched-transactions", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse suggestMatchTransaction(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transactionSearchService.suggestTransactionsForMatch(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching unmatched sell transactions", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching unmatched sell transactions");
        }

    }

    @RequestMapping(value = "find-all-user-wallet-transfer-transactions-paged", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse findAllUserWalletTransferTransactionsPaged(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transactionSearchService.findAllUserWalletTransferTransactionsPaged(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet transfer transactions by user details ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet transfer transactions for logged in user");
        }

    }


    @RequestMapping(value = "match-transaction", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse matchTransaction(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transferService.matchTransactionByCustomer(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching unmatched sell transactions", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching unmatched sell transactions");
        }

    }

    @RequestMapping(value = "compute-charges-and-values-for-match", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse computeChargesAndValuesForMatchTransaction(@RequestBody WalletTransferRequest walletTransferRequest)  {

        try {
            return transferService.computeChargesForMatchTransaction(walletTransferRequest);
        } catch (Exception e) {
            logger.error("Error occurred while computing wallet transfer transactions charge and values ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while computing charges and values");
        }

    }

    @RequestMapping(value = "user-funded-transactions", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse fetchFundedTransactionsForUser(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transactionSearchService.fetchFundedTransactionDetailsByPage(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while initiating wallet transfer transactions for user by admin user", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions for user");
        }

    }

    @RequestMapping(value = "user-unfunded-transactions", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse fetchUnFundedTransactionsForUser(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transactionSearchService.fetchUnFundedTransactionDetailsByPage(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while initiating wallet transfer transactions for user by admin user", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoing transactions for user");
        }

    }

    @RequestMapping(value = "suggest-transactions-by-rate-and-amount", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse suggestTransactionsByUserRateAndAmount(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transactionSearchService.suggestTransactionsByUserRateAndAmount(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching unmatched sell transactions", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching unmatched sell transactions");
        }

    }

    @RequestMapping(value = "suggest-transactions-by-exact-rate-and-amount", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse suggestTransactionsByExactUserRateAndAmount(@RequestBody WalletTransferRequest walletTransferRequest, HttpServletRequest servletRequest)  {

        try {
            return transactionSearchService.suggestTransactionsByExactUserRateAndAmount(walletTransferRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching unmatched sell transactions", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching unmatched sell transactions");
        }

    }

    @RequestMapping(value = "/search-transactions-or", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse searchTransactionsByOrOptions(@RequestBody TransferTransactionSearchRequestModel transactionSearchRequestModel) {

        try {
            return transactionSearchService.searchTransactionsByOrOptions(transactionSearchRequestModel);
        } catch (Exception e) {
            logger.error("System error occurred while fetching transactions by options due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while searching");
        }

    }


    @RequestMapping(value = "/search-transactions-and", method = RequestMethod.POST)
    @ResponseBody
    public WalletTransferRequestResponse searchTransactionsByAndOptions(@RequestBody TransferTransactionSearchRequestModel transactionSearchRequestModel) {

        try {
            return transactionSearchService.searchTransactionsByAndOptions(transactionSearchRequestModel);
        } catch (Exception e) {
            logger.error("System error occurred while fetching transactions by options due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while searching");
        }

    }

    @PreAuthorize("hasAuthority('SETTLE_CUSTOMER')")
    @GetMapping("/settleCustomer")
    @ResponseBody
    public WalletTransferRequestResponse settleCustomer(@RequestParam("id") Long id, HttpServletRequest servletRequest){
        try {
            return transferService.settleCustomer(id, servletRequest);
        } catch (Exception e) {
            logger.error("System error occurred while settling transaction due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while settling customer");
        }
    }

    @GetMapping("/get-dashboard-details")
    @ResponseBody
    public TransferDashboardDetails getTransactionDashboardDetailsByDateRange(){
            return transactionSearchService.getDashboardDetailsForTransactions();
    }

    @GetMapping("/get-revenue-dashboard-details")
    @ResponseBody
    public RevenueDashboardDetails getDashboardDetailsForRevenue(){
            return transactionSearchService.getDashboardDetailsForRevenue();
    }

    @PostMapping("/download-report-with-params")
    @ResponseBody
    public void downloadReportWithParams(@RequestBody TransferTransactionSearchRequestModel requestModel, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        try {
            transactionDownloadService.generateExcelForTransactions(requestModel, servletResponse, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while generating report with params due to ", e);
        }
    }

    @GetMapping("/cancel-bid")
    @ResponseBody
    public WalletTransferRequestResponse cancleBid(@RequestParam("id") Long id, HttpServletRequest servletRequest) {
        try {
            return transferService.cancleBid(id, servletRequest);
        } catch (Exception e) {
            logger.error("System error occurred while cancelling bid due to ", e);
            return WalletTransferRequestResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while cancelling customer bid");
        }
    }


    @GetMapping("/get-bid-dashboard-details")
    @ResponseBody
    public BidDashboardDetails getBidDashboardDetails(){
        return transactionSearchService.getDashboardDetailsForBid();
    }


}
