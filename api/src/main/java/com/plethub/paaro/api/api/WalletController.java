package com.plethub.paaro.api.api;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletRequest;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletTransactionSearchRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.WalletBalanceDashboardDetails;
import com.plethub.paaro.core.appservice.apiresponsemodel.WalletResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.services.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    Logger logger = LoggerFactory.getLogger(WalletController.class.getName());

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseBody
    public WalletResponse findWalletById(@RequestParam("id") Long id)  {

        try {
            return walletService.findWalletById(id);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet by Id", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet by id");
        }
    }


    @RequestMapping(value = "/get_by_user_id", method = RequestMethod.GET)
    @ResponseBody
    public WalletResponse findWalletByUserId(@RequestParam("userid") Long userId)  {

        try {
            return walletService.findWalletsByUserId(userId);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet by user Id", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet by user id");
        }
    }


    @RequestMapping(value = "/get_by_email", method = RequestMethod.GET)
    @ResponseBody
    public WalletResponse findWalletByEmail(@RequestParam("email") String email)  {

        try {
            return walletService.findWalletsByEmail(email);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet by email", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet by email");
        }
    }

    @RequestMapping(value = "get_by_currency", method = RequestMethod.GET)
    @ResponseBody
    public WalletResponse findWalletByCurrency(@RequestParam("currency") String currency)  {

        try {
            return walletService.findWalletsByCurrency(currency);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet by currency", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet by currency");
        }
    }

    @RequestMapping(value = "get_loggedin_user_wallets", method = RequestMethod.GET)
    @ResponseBody
    public WalletResponse findLoggedInUserWallets()  {

        try {
            return walletService.findLoggedInUserWallets();
        } catch (Exception e) {
            logger.error("Error occurred while fetching logged in user wallets ", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching logged in user wallet");
        }
    }

    @RequestMapping(value = "create_wallet", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse addWallet(@RequestBody WalletRequest walletRequest, HttpServletRequest servletRequest)  {

        try {
            return walletService.addWallet(walletRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while mapping wallet to user", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while adding wallet");
        }
    }

    
    @RequestMapping(value = "fund_wallet", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse fundWallet(@RequestBody WalletRequest walletRequest, HttpServletRequest servletRequest)  {

        try {
            return walletService.fundWallet(walletRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while funding wallet ", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while funding wallet");
        }
        
    }

    @RequestMapping(value = "find_wallet_funding_transactions", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse findAllWalletTransactions(@RequestBody WalletRequest walletRequest)  {

        try {
            return walletService.findAllFundingWalletTransactionsByUserWallet(walletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet funding transactions by currency ", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet funding transactions");
        }

    }

    @RequestMapping(value = "find_wallet_funding_transactions/paged", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse findAllWalletTransactionsPaged(@RequestBody WalletRequest walletRequest)  {

        try {
            return walletService.findALlFundingWalletTransactionsByUserWalletPaged(walletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching paged wallet funding transactions by currency", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet funding transactions paged");
        }

    }

    @RequestMapping(value = "find_wallet_funding_transactions/by_email/paged", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse findAllWalletTransactionsByEmailPaged(@RequestBody WalletRequest walletRequest)  {

        try {
            return walletService.findALlFundingWalletTransactionsByUserWalletPaged(walletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching paged wallet funding transactions by currency", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet by currency");
        }

    }

    @RequestMapping(value = "find_wallet_funding_transactions_by_email_and_currency", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse findAllWalletTransactionsByEmailAndCurrency(@RequestBody WalletRequest walletRequest)  {

        try {
            return walletService.findALlFundingWalletTransactionsByEmailAndCurrency(walletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching wallet funding transactions by email and currency", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet by currency and email");
        }

    }
    @RequestMapping(value = "find_wallet_funding_transactions_by_email_and_currency/paged", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse findAllWalletTransactionsByEmailAndCurrencyPaged(@RequestBody WalletRequest walletRequest)  {

        try {
            return walletService.findALlFundingWalletTransactionsByEmailAndCurrencyPaged(walletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while fetching paged wallet funding transactions by email and currency", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet by currency and email");
        }

    }


    @RequestMapping(value = "fund-wallet-and-transaction", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse fundWalletAndTransaction(@RequestBody WalletRequest walletRequest, HttpServletRequest servletRequest)  {

        try {
            return walletService.fundWalletAndTransaction(walletRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while funding wallet and transaction ", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while funding wallet and transaction");
        }

    }

    @RequestMapping(value = "fund-transaction-from-wallet", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse fundTransaction(@RequestBody WalletRequest walletRequest, HttpServletRequest servletRequest)  {

        try {
            return walletService.fundTransactionFromWallet(walletRequest, servletRequest);
        } catch (Exception e) {
            logger.error("Error occurred while funding transaction ", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while funding transaction");
        }

    }


    @RequestMapping(value = "/funding/search-transactions-or", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse searchTransactionsByOrOptions(@RequestBody WalletTransactionSearchRequestModel transactionSearchRequestModel) {

        try {
            return walletService.searchTransactionsByOrOptions(transactionSearchRequestModel);
        } catch (Exception e) {
            logger.error("System error occurred while fetching transactions by options due to ", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while searching");
        }

    }

    @RequestMapping(value = "/funding/search-transactions-and", method = RequestMethod.POST)
    @ResponseBody
    public WalletResponse searchTransactionsByAndOptions(@RequestBody WalletTransactionSearchRequestModel transactionSearchRequestModel) {

        try {
            return walletService.searchTransactionsByAndOptions(transactionSearchRequestModel);
        } catch (Exception e) {
            logger.error("System error occurred while fetching transactions by options due to ", e);
            return WalletResponse.returnResponseWithCode(ApiResponseCode.SYSTEM_ERROR, "System error occurred while searching");
        }

    }

    @RequestMapping(value = "/wallet-liquidity", method = RequestMethod.GET)
    @ResponseBody
    public WalletBalanceDashboardDetails getWalletBalanceDashboardDetails() {

        try {
            return walletService.getWalletBalanceDashboardDetails();
        } catch (Exception e) {
            logger.error("System error occurred while wallet balance by options due to ", e);
            return WalletBalanceDashboardDetails.fromNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching wallet balance");
        }

    }

}
