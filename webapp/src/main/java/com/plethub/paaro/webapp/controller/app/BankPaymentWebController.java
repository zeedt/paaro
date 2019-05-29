package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.appservice.apirequestmodel.BankDepositRequestModel;
import com.plethub.paaro.core.appservice.apirequestmodel.TransferTransactionSearchRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankDepositResponseModel;
import com.plethub.paaro.core.appservice.enums.DepositStatus;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.appservice.repository.WalletTransferTransactionRepository;
import com.plethub.paaro.core.appservice.services.BankDepositService;
import com.plethub.paaro.core.appservice.services.CurrencyService;
import com.plethub.paaro.core.appservice.services.TransactionSearchService;
import com.plethub.paaro.core.models.BankDeposit;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;


@Controller
@RequestMapping("/app/payment")
public class BankPaymentWebController {

    @Autowired
    BankDepositService bankDepositService;
    @Autowired
    CurrencyService currencyService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void init(HttpServletRequest httpServletRequest, Model model) {
        model.addAttribute("statuses", DepositStatus.getDepositStatus());
        model.addAttribute("currencies", currencyService.getActiveCurrencies());

    }

    @GetMapping
    public String all(Model model){
        return "payment/view";
    }

    @GetMapping("/search")
    public @ResponseBody
    DataTablesOutput<BankDeposit> searchRevisedEntity(
            DataTablesInput input, @RequestParam("depositStatus") String depositStatus, @RequestParam("userEmail") String userEmail, @RequestParam("paymentReferenceNo") String paymentReferenceNo,
            @RequestParam("tellerNumber") String tellerNumber, @RequestParam("amount") String amount,@RequestParam("currencyType") String currencyType)
    {
        Pageable pageables = DataTablesUtils.getPageable(input);
        //Page<BankDeposit> audit = bankDepositService.getAllBankDeposits(pageables).getBankDepositPage();
        Page<BankDeposit> audit = null;


        if(!StringUtils.isNoneBlank(depositStatus) && !StringUtils.isNoneBlank(userEmail)
                && !StringUtils.isNoneBlank(tellerNumber) && !StringUtils.isNoneBlank(amount)&& !StringUtils.isNoneBlank(currencyType)&& !StringUtils.isNoneBlank(paymentReferenceNo) ){

            audit=bankDepositService.searchForBankDepositModelPageapbleWithAnd(pageables, new BankDepositRequestModel()).getBankDepositPage();

        }
        else{

            DepositStatus status = null;
            if (StringUtils.isNoneBlank(depositStatus)){
                status = DepositStatus.valueOf(depositStatus);
            }
            BigDecimal amt = null;
            if (StringUtils.isNoneBlank(amount)){
                amt = new BigDecimal(amount);
                System.out.println(amt);
            }
            BankDepositRequestModel transferTransactionSearchRequestModel = new BankDepositRequestModel(currencyType, amt, tellerNumber, paymentReferenceNo, null, status, userEmail);
            audit=  bankDepositService.searchForBankDepositModelPageapbleWithAnd(pageables,transferTransactionSearchRequestModel).getBankDepositPage();
            logger.info("the search query is {}",transferTransactionSearchRequestModel);

        }

        DataTablesOutput<BankDeposit> out = new DataTablesOutput<BankDeposit>();
        out.setDraw(input.getDraw());
        out.setData(audit.getContent());
        out.setRecordsFiltered(audit.getTotalElements());
        out.setRecordsTotal(audit.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/detail")
    public String customerDetail(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            BankDeposit bankDeposit = bankDepositService.getDetails(id);
            boolean canConfirm = false;
            if (bankDeposit.getTransactionStatus().equals(DepositStatus.PAYMENT_AWAITING_VERIFICATION)){
                canConfirm = true;
            }
            model.addAttribute("details", bankDeposit);
            model.addAttribute("confirm", canConfirm);
            return "payment/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/payment";
        }

    }

    @GetMapping("/{id}/approve")
    public String approveDetail(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            BankDepositResponseModel kycResponseModel = bankDepositService.approveBankDepositForUser(id, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", kycResponseModel.getNarration());
            return "redirect:/app/payment/"+ id+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/payment/"+ id+"/detail";
        }

    }
}
