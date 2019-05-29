package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.appservice.apirequestmodel.BankDepositRequestModel;
import com.plethub.paaro.core.appservice.apirequestmodel.CashOutRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.BankDepositResponseModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.CashOutResponse;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.CashOutStatus;
import com.plethub.paaro.core.appservice.enums.DepositStatus;
import com.plethub.paaro.core.appservice.services.BankDepositService;
import com.plethub.paaro.core.appservice.services.CashOutService;
import com.plethub.paaro.core.appservice.services.CurrencyService;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.models.BankDeposit;
import com.plethub.paaro.core.models.CashOutLog;
import com.plethub.paaro.webapp.service.BankService;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;

@Controller
@RequestMapping("/app/cashout")
public class CashOutController {

    @Autowired
    CashOutService cashOutService;
    @Autowired
    BankService bankService;
    @Autowired
    CurrencyService currencyService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @ModelAttribute
    public void init(HttpServletRequest httpServletRequest, Model model) {
        model.addAttribute("statuses", CashOutStatus.getCashOutStatus());
        model.addAttribute("currencies", currencyService.getActiveCurrencies());
    }

    @GetMapping
    public String all(Model model){
        return "cashout/view";
    }

    @GetMapping("/search")
    public @ResponseBody
    DataTablesOutput<CashOutLog> searchRevisedEntity(
            DataTablesInput input, @RequestParam("walletId") String walletId, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName,
            @RequestParam("cashOutStatus") String cashOutStatus, @RequestParam("email") String email, @RequestParam("currencyType") String currencyType, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate)
    {
        Pageable pageables = DataTablesUtils.getPageable(input);
        Page<CashOutLog> audit = null;


        if(!StringUtils.isNoneBlank(walletId) && !StringUtils.isNoneBlank(firstName) && !StringUtils.isNoneBlank(lastName)
                && !StringUtils.isNoneBlank(cashOutStatus) && !StringUtils.isNoneBlank(email) && !StringUtils.isNoneBlank(currencyType) && !StringUtils.isNoneBlank(fromDate)
                && !StringUtils.isNoneBlank(toDate)){

            audit=cashOutService.searchCashOutWithAnd(pageables, new CashOutRequest()).getCashOutLogPage();

        }
        else{

            CashOutStatus status = null;
            if (StringUtils.isNoneBlank(cashOutStatus)){
                status = CashOutStatus.valueOf(cashOutStatus);
            }
            Long wallet = null;
            if (StringUtils.isNoneBlank(walletId)){
                wallet = Long.valueOf(walletId);
                System.out.println(wallet);
            }
            CashOutRequest cashOutRequest = new CashOutRequest(wallet, firstName, lastName, currencyType, DateFormatter.formatString(fromDate),DateFormatter.formatString(toDate), email, status);
            audit=  cashOutService.searchCashOutWithAnd(pageables,cashOutRequest).getCashOutLogPage();
            logger.info("the search query is {}",cashOutRequest);

        }

        DataTablesOutput<CashOutLog> out = new DataTablesOutput<CashOutLog>();
        out.setDraw(input.getDraw());
        out.setData(audit.getContent());
        out.setRecordsFiltered(audit.getTotalElements());
        out.setRecordsTotal(audit.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/detail")
    public String customerDetail(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            CashOutLog cashOut = cashOutService.getCashOut(id);
            model.addAttribute("cashOut", cashOut);
            Bank bank = bankService.getBankDetails(cashOut.getBankId());
            model.addAttribute("bank", bank);
            CashOutRequest cashOutRequest = new CashOutRequest();
            cashOutRequest.setCashOutLogId(cashOut.getId());
            model.addAttribute("cashOutRequest", cashOutRequest);
            return "cashout/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/cashout";
        }

    }

    @PostMapping(path = "/update", params = "action=Approve")
    public String approveDetail(@Valid CashOutRequest cashOutReq, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        try {
            CashOutResponse cashOutResponse = cashOutService.approveCashOutForCustomer(cashOutReq, httpServletRequest);
            if (cashOutResponse.getApiResponseCode().equals(ApiResponseCode.SUCCESSFUL)){
                redirectAttributes.addFlashAttribute("message", cashOutResponse.getNarration());
                return "redirect:/app/cashout";
            }else {
                CashOutLog cashOut = cashOutService.getCashOut(cashOutReq.getCashOutLogId());
                model.addAttribute("cashOut", cashOut);
                Bank bank = bankService.getBankDetails(cashOut.getBankId());
                model.addAttribute("bank", bank);
                CashOutRequest cashOutRequest = new CashOutRequest();
                cashOutRequest.setCashOutLogId(cashOut.getId());
                model.addAttribute("cashOutRequest", cashOutRequest);
                model.addAttribute("failure",cashOutResponse.getNarration());
                return "cashout/detail";
            }

        } catch (Exception e) {
            CashOutLog cashOut = cashOutService.getCashOut(cashOutReq.getCashOutLogId());
            model.addAttribute("cashOut", cashOut);
            Bank bank = bankService.getBankDetails(cashOut.getBankId());
            model.addAttribute("bank", bank);
            CashOutRequest cashOutRequest = new CashOutRequest();
            cashOutRequest.setCashOutLogId(cashOut.getId());
            model.addAttribute("cashOutRequest", cashOutRequest);
            logger.error("Error updating cashout",e);
            model.addAttribute("failure",e.getMessage());
            return "cashout/detail";
        }

    }

    @PostMapping(path = "/update", params = "action=Decline")
    public String declineDetail(@Valid CashOutRequest cashOutReq, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
        try {
            CashOutResponse cashOutResponse = cashOutService.declineCashOutForCustomer(cashOutReq, httpServletRequest);
            if (cashOutResponse.getApiResponseCode().equals(ApiResponseCode.SUCCESSFUL)){
                redirectAttributes.addFlashAttribute("message", cashOutResponse.getNarration());
                return "redirect:/app/cashout";
            }else {
                CashOutLog cashOut = cashOutService.getCashOut(cashOutReq.getCashOutLogId());
                model.addAttribute("cashOut", cashOut);
                Bank bank = bankService.getBankDetails(cashOut.getBankId());
                model.addAttribute("bank", bank);
                CashOutRequest cashOutRequest = new CashOutRequest();
                cashOutRequest.setCashOutLogId(cashOut.getId());
                model.addAttribute("cashOutRequest", cashOutRequest);
                model.addAttribute("failure",cashOutResponse.getNarration());
                return "cashout/detail";
            }
        } catch (Exception e) {
            CashOutLog cashOut = cashOutService.getCashOut(cashOutReq.getCashOutLogId());
            model.addAttribute("cashOut", cashOut);
            Bank bank = bankService.getBankDetails(cashOut.getBankId());
            model.addAttribute("bank", bank);
            CashOutRequest cashOutRequest = new CashOutRequest();
            cashOutRequest.setCashOutLogId(cashOut.getId());
            model.addAttribute("cashOutRequest", cashOutRequest);
            logger.error("Error updating cashout",e);
            model.addAttribute("failure",e.getMessage());
            return "cashout/detail";
        }

    }

}
