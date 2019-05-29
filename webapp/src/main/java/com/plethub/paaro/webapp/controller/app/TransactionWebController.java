package com.plethub.paaro.webapp.controller.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.plethub.paaro.core.appservice.apirequestmodel.TransferTransactionSearchRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.WalletTransferRequestResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.appservice.repository.WalletTransferTransactionRepository;
import com.plethub.paaro.core.appservice.services.CurrencyService;
import com.plethub.paaro.core.appservice.services.TransactionDownloadService;
import com.plethub.paaro.core.appservice.services.TransactionSearchService;
import com.plethub.paaro.core.appservice.services.TransferService;
import com.plethub.paaro.core.authority.services.AuthorityService;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import com.plethub.paaro.webapp.util.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Controller
@RequestMapping("/app/transaction")
public class TransactionWebController {

    @Autowired
    TransactionSearchService transactionService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    TransactionDownloadService transactionDownloadService;
    @Autowired
    TransferService transferService;
    @Autowired
    WalletTransferTransactionRepository walletTransferTransactionRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void init(HttpServletRequest httpServletRequest, Model model) {
        model.addAttribute("statuses", TransactionStatus.getTransactionStatus());
        model.addAttribute("currencies", currencyService.getActiveCurrencies());

    }

    @GetMapping
    public String all(Model model){
        return "transaction/view";
    }

    @GetMapping("/pending")
    public String pending(Model model){
        return "transaction/pending";
    }

    @GetMapping("/search")
    public @ResponseBody
    DataTablesOutput<WalletTransferTransaction> searchRevisedEntity(
            DataTablesInput input, @RequestParam("paaroReferenceId") String paaroReferenceId, @RequestParam("fromCurrency") String fromCurrency, @RequestParam("toCurrency") String toCurrency,
            @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate, @RequestParam("accountNumber") String accountNumber,
            @RequestParam("transactionStatus") String transactionStatus, @RequestParam("isSettled") String isSettled, @RequestParam("email") String email)
    {

        logger.info("the search details reference {},fromDate {}, endDate {}, id {} ",paaroReferenceId,fromDate,toDate,transactionStatus);

        Pageable pageables = DataTablesUtils.getPageable(input);
        Page<WalletTransferTransaction> audit = null;

        if(!StringUtils.isNoneBlank(paaroReferenceId) && !StringUtils.isNoneBlank(fromDate)
                && !StringUtils.isNoneBlank(toDate) && !StringUtils.isNoneBlank(transactionStatus)
                && !StringUtils.isNoneBlank(fromCurrency) && !StringUtils.isNoneBlank(toCurrency) && !StringUtils.isNoneBlank(isSettled)&& !StringUtils.isNoneBlank(accountNumber) ){

            audit=transactionService.searchTransactionsByAndOptionsPageable(pageables, new TransferTransactionSearchRequestModel()).getWalletTransferTransactionPage();

        }
        else{

            TransactionStatus status = null;
            if (StringUtils.isNoneBlank(transactionStatus)){
                status = TransactionStatus.valueOf(transactionStatus);
            }
            TransferTransactionSearchRequestModel transferTransactionSearchRequestModel = new TransferTransactionSearchRequestModel(null, accountNumber, paaroReferenceId, null, status, DateFormatter.formatString(fromDate),DateFormatter.formatString(toDate), fromCurrency, toCurrency, email, Boolean.valueOf(isSettled), null, null);
            audit=  transactionService.searchTransactionsByAndOptionsPageable(pageables,transferTransactionSearchRequestModel).getWalletTransferTransactionPage();
            logger.info("the search query is {}",transferTransactionSearchRequestModel);

        }
        DataTablesOutput<WalletTransferTransaction> out = new DataTablesOutput<WalletTransferTransaction>();
        out.setDraw(input.getDraw());
        out.setData(audit.getContent());
        out.setRecordsFiltered(audit.getTotalElements());
        out.setRecordsTotal(audit.getTotalElements());
        return out;
    }

    @GetMapping("/payment")
    public @ResponseBody
    DataTablesOutput<WalletTransferTransaction> searchPendingRevisedEntity(
            DataTablesInput input, @RequestParam("paaroReferenceId") String paaroReferenceId, @RequestParam("fromCurrency") String fromCurrency, @RequestParam("toCurrency") String toCurrency,
            @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate, @RequestParam("email") String email, @RequestParam("accountNumber") String accountNumber)
    {

        logger.info("the search details reference {},fromDate {}, endDate {}, id {} ",paaroReferenceId,fromDate,toDate);

        Pageable pageables = DataTablesUtils.getPageable(input);
        Page<WalletTransferTransaction> audit = null;
        List<TransactionStatus> transactionStatusList = new ArrayList<>();
        transactionStatusList.add(TransactionStatus.CUSTOMER_MAPPED_REQUEST);
        transactionStatusList.add(TransactionStatus.SYSTEM_MAPPED_REQUEST);
        transactionStatusList.add(TransactionStatus.COMPLETED);
        if(!StringUtils.isNoneBlank(paaroReferenceId) && !StringUtils.isNoneBlank(fromDate)
                && !StringUtils.isNoneBlank(toDate)
                && !StringUtils.isNoneBlank(fromCurrency) && !StringUtils.isNoneBlank(toCurrency) && !StringUtils.isNoneBlank(accountNumber)){

            TransferTransactionSearchRequestModel transferTransactionSearchRequestModel = new TransferTransactionSearchRequestModel();
            transferTransactionSearchRequestModel.setSettled(false);
            transferTransactionSearchRequestModel.setStatuses(transactionStatusList);
            audit=transactionService.searchTransactionsByAndOptionsPageable(pageables, transferTransactionSearchRequestModel).getWalletTransferTransactionPage();

        }
        else{

            TransferTransactionSearchRequestModel transferTransactionSearchRequestModel = new TransferTransactionSearchRequestModel(null, accountNumber, paaroReferenceId, null, null, DateFormatter.formatString(fromDate),DateFormatter.formatString(toDate), fromCurrency, toCurrency, email, false, null, null);
            transferTransactionSearchRequestModel.setStatuses(transactionStatusList);
            audit=  transactionService.searchTransactionsByAndOptionsPageable(pageables,transferTransactionSearchRequestModel).getWalletTransferTransactionPage();
            logger.info("the search query is {}",transferTransactionSearchRequestModel);

        }
        DataTablesOutput<WalletTransferTransaction> out = new DataTablesOutput<WalletTransferTransaction>();
        out.setDraw(input.getDraw());
        out.setData(audit.getContent());
        out.setRecordsFiltered(audit.getTotalElements());
        out.setRecordsTotal(audit.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/detail")
    public String customerDetail(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            WalletTransferTransaction walletTransferTransaction =  walletTransferTransactionRepository.getOne(id);
//            if (null != walletTransferTransaction.getTransferRequestMap()){
//                    if ("NGN".equalsIgnoreCase(walletTransferTransaction.getFromCurrency().getType())){
//                        model.addAttribute("details", walletTransferTransaction.getTransferRequestMap().getNairaHolderTransaction());
//                    }else {
//                        model.addAttribute("details", walletTransferTransaction.getTransferRequestMap().getOtherCurrencyHolderTransaction());
//                    }
//            }
            model.addAttribute("details", walletTransferTransaction);
            return "transaction/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/transaction";
        }
    }

    @GetMapping("/{id}/settle")
    public String settleCustomer(@PathVariable("id") Long id, HttpServletRequest servletRequest, RedirectAttributes redirectAttributes){
        try {
            WalletTransferRequestResponse walletTransferRequestResponse = transferService.settleCustomer(id, servletRequest);
            redirectAttributes.addFlashAttribute("message", walletTransferRequestResponse.getMessage());
            return "redirect:/app/transaction/"+id+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/transaction/"+id+"/detail";
        }
    }

    @PostMapping("/download")
    public String downloadCSV(RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Model model) {
        try {
            String isSettled = httpServletRequest.getParameter("settled");
            String paaroReferenceId = httpServletRequest.getParameter("paaroReferenceId");
            String fromDate = httpServletRequest.getParameter("fromDate");
            String toDate = httpServletRequest.getParameter("toDate");
            String fromCurrency = httpServletRequest.getParameter("fromCurrency");
            String toCurrency = httpServletRequest.getParameter("toCurrency");
            String transactionStatus = httpServletRequest.getParameter("transactionStatus");
            String accountNumber = httpServletRequest.getParameter("accountNumber");
            String email = httpServletRequest.getParameter("email");
            TransferTransactionSearchRequestModel transferTransactionSearchRequestModel = null;

            if(!StringUtils.isNoneBlank(paaroReferenceId) && !StringUtils.isNoneBlank(fromDate)
                    && !StringUtils.isNoneBlank(toDate) && !StringUtils.isNoneBlank(transactionStatus)
                    && !StringUtils.isNoneBlank(fromCurrency) && !StringUtils.isNoneBlank(toCurrency) && !StringUtils.isNoneBlank(isSettled)&& !StringUtils.isNoneBlank(accountNumber) ){

                    transferTransactionSearchRequestModel = new TransferTransactionSearchRequestModel();
            }
            else{

                TransactionStatus status = null;
                if (StringUtils.isNoneBlank(transactionStatus)){
                    status = TransactionStatus.valueOf(transactionStatus);
                }
                transferTransactionSearchRequestModel = new TransferTransactionSearchRequestModel(null, accountNumber, paaroReferenceId, null, status, DateFormatter.formatString(fromDate),DateFormatter.formatString(toDate), fromCurrency, toCurrency, email, Boolean.valueOf(isSettled), null, null);
            }

            transactionDownloadService.generateExcelForTransactions(transferTransactionSearchRequestModel,httpServletResponse,httpServletRequest);
            return null;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/transaction";
        }
    }
}
