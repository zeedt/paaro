package com.plethub.paaro.webapp.controller.app;

import com.plethub.paaro.core.appservice.apirequestmodel.CashOutRequest;
import com.plethub.paaro.core.appservice.apirequestmodel.TransferTransactionSearchRequestModel;
import com.plethub.paaro.core.appservice.apirequestmodel.WalletTransactionSearchRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.KycResponseModel;
import com.plethub.paaro.core.appservice.enums.CashOutStatus;
import com.plethub.paaro.core.appservice.enums.PaymentMethod;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.appservice.repository.WalletFundingTransactionRepository;
import com.plethub.paaro.core.appservice.repository.WalletTransferTransactionRepository;
import com.plethub.paaro.core.appservice.services.*;
import com.plethub.paaro.core.authority.services.AuthorityService;
import com.plethub.paaro.core.models.CashOutLog;
import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.models.WalletFundingTransaction;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import com.plethub.paaro.core.notes.NoteService;
import com.plethub.paaro.core.notes.dto.NoteDTO;
import com.plethub.paaro.core.usermanagement.apimodels.ManagedUserModelApi;
import com.plethub.paaro.core.usermanagement.enums.ResponseStatus;
import com.plethub.paaro.core.usermanagement.enums.UserCategory;
import com.plethub.paaro.core.usermanagement.service.UserService;
import com.plethub.paaro.webapp.exception.PaaroException;
import com.plethub.paaro.webapp.util.DateFormatter;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/app/customer")
public class CustomerWebController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserService userService;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    CashOutService cashOutService;

    @Autowired
    WalletService walletService;

    @Autowired
    KycService kycService;

    @Autowired
    AuthorityService authorityService;

    @Autowired
    NoteService noteService;

    @Autowired
    TransactionSearchService transactionSearchService;

    @ModelAttribute
    public void init(HttpServletRequest httpServletRequest, Model model) {
        model.addAttribute("statuses", TransactionStatus.getTransactionStatus());
        model.addAttribute("methods", PaymentMethod.getPaymentMethods());
        model.addAttribute("currencies", currencyService.getActiveCurrencies());
    }

    @GetMapping
    public String customer(){
        return "customer/view";
    }

    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<ManagedUser> getUsers(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<ManagedUser> sq = userService.getCustomerUserDetails(pageable).getManagedUserPage();
        DataTablesOutput<ManagedUser> out = new DataTablesOutput<ManagedUser>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping(path = "/wallet/transactions")
    public
    @ResponseBody
    DataTablesOutput<WalletFundingTransaction> getcustomerWalletTransactions(DataTablesInput input, @RequestParam("csearch") String id, @RequestParam("paaroReferenceId") String paaroReferenceId, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                                                                             @RequestParam("transactionStatus") String transactionStatus, @RequestParam("paymentMethod") String paymentMethod,@RequestParam("currency") String currency) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        //Page<WalletFundingTransaction> sq = walletService.searchTransactionsByAndOptionsPageable(pageable);
        Page<WalletFundingTransaction> sq = null;

        if(!StringUtils.isNoneBlank(paaroReferenceId) && !StringUtils.isNoneBlank(fromDate)
                && !StringUtils.isNoneBlank(toDate) && !StringUtils.isNoneBlank(transactionStatus)
                && !StringUtils.isNoneBlank(currency) && !StringUtils.isNoneBlank(paymentMethod) ){

            WalletTransactionSearchRequestModel transferTransactionSearchRequestModel = new WalletTransactionSearchRequestModel();
            transferTransactionSearchRequestModel.setUserId(Long.valueOf(id));
            sq=walletService.searchTransactionsByAndOptionsPageable(pageable, transferTransactionSearchRequestModel).getWalletFundingTransactionPage();

        }
        else{

            PaymentMethod method = null;
            if (StringUtils.isNoneBlank(paymentMethod)){
                method = PaymentMethod.valueOf(paymentMethod);
            }
            TransactionStatus status = null;
            if (StringUtils.isNoneBlank(transactionStatus)){
                status = TransactionStatus.valueOf(transactionStatus);
            }
            WalletTransactionSearchRequestModel transferTransactionSearchRequestModel = new WalletTransactionSearchRequestModel(paaroReferenceId,status,DateFormatter.formatString(fromDate),DateFormatter.formatString(toDate),currency,Long.valueOf(id),null,method);
            sq=  walletService.searchTransactionsByAndOptionsPageable(pageable,transferTransactionSearchRequestModel).getWalletFundingTransactionPage();
            logger.info("the search query is {}",transferTransactionSearchRequestModel);

        }
        DataTablesOutput<WalletFundingTransaction> out = new DataTablesOutput<WalletFundingTransaction>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping(path = "/wallet/cashout")
    public
    @ResponseBody
    DataTablesOutput<CashOutLog> getcustomerWalletCashOuts(DataTablesInput input, @RequestParam("csearch") String email,@RequestParam("cwallet") String wallet, @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate,
                                                           @RequestParam("cashOutStatus") String cashOutStatus, @RequestParam("currency") String currency) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<CashOutLog> sq = null;

        if(!StringUtils.isNoneBlank(fromDate)
                && !StringUtils.isNoneBlank(toDate) && !StringUtils.isNoneBlank(cashOutStatus)
                && !StringUtils.isNoneBlank(currency)){

            CashOutRequest cashOutRequest = new CashOutRequest();
            cashOutRequest.setEmail(email);
            sq=cashOutService.searchCashOutWithAnd(pageable, cashOutRequest).getCashOutLogPage();

        }
        else{
            CashOutStatus status = null;
            if (StringUtils.isNoneBlank(cashOutStatus)){
                status = CashOutStatus.valueOf(cashOutStatus);
            }
            CashOutRequest cashOutRequest = new CashOutRequest(Long.valueOf(wallet), null,null,currency,DateFormatter.formatString(fromDate),DateFormatter.formatString(toDate),email,status);
            sq=  cashOutService.searchCashOutWithAnd(pageable,cashOutRequest).getCashOutLogPage();
            logger.info("the search query is {}",cashOutRequest);

        }
        DataTablesOutput<CashOutLog> out = new DataTablesOutput<CashOutLog>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping(path = "/request/transactions")
    public
    @ResponseBody
    DataTablesOutput<WalletTransferTransaction> getcustomerRequests(DataTablesInput input, @RequestParam("csearch") String id, @RequestParam("paaroReferenceId") String paaroReferenceId, @RequestParam("fromCurrency") String fromCurrency, @RequestParam("toCurrency") String toCurrency,
                                                                    @RequestParam("fromDate") String fromDate, @RequestParam("toDate") String toDate, @RequestParam("accountNumber") String accountNumber, @RequestParam("transactionStatus") String transactionStatus, @RequestParam("isSettled") String isSettled) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        //Page<WalletTransferTransaction> sq = walletTransferTransactionRepository.findAllByTransactionStatusInAndManagedUser_Id(TransactionStatus.getTransactionStatus(),Long.parseLong(id),pageable);
        Page<WalletTransferTransaction> sq = null;

        if(!StringUtils.isNoneBlank(paaroReferenceId) && !StringUtils.isNoneBlank(fromDate)
                && !StringUtils.isNoneBlank(toDate) && !StringUtils.isNoneBlank(isSettled) && !StringUtils.isNoneBlank(transactionStatus)
                && !StringUtils.isNoneBlank(fromCurrency) && !StringUtils.isNoneBlank(toCurrency) && !StringUtils.isNoneBlank(accountNumber)){

            TransferTransactionSearchRequestModel transferTransactionSearchRequestModel = new TransferTransactionSearchRequestModel();
            transferTransactionSearchRequestModel.setUserId(Long.valueOf(id));
            sq=transactionSearchService.searchTransactionsByAndOptionsPageable(pageable, transferTransactionSearchRequestModel).getWalletTransferTransactionPage();

        }
        else{

            TransactionStatus status = null;
            if (StringUtils.isNoneBlank(transactionStatus)){
                status = TransactionStatus.valueOf(transactionStatus);
            }
            TransferTransactionSearchRequestModel transferTransactionSearchRequestModel = new TransferTransactionSearchRequestModel(null, accountNumber, paaroReferenceId, null, status, DateFormatter.formatString(fromDate),DateFormatter.formatString(toDate), fromCurrency, toCurrency, null, Boolean.valueOf(isSettled), null, null);
            sq=  transactionSearchService.searchTransactionsByAndOptionsPageable(pageable,transferTransactionSearchRequestModel).getWalletTransferTransactionPage();
            logger.info("the search query is {}",transferTransactionSearchRequestModel);

        }
        DataTablesOutput<WalletTransferTransaction> out = new DataTablesOutput<WalletTransferTransaction>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping(path = "/notes")
    public
    @ResponseBody
    DataTablesOutput<NoteDTO> getcustomerNotes(DataTablesInput input, @RequestParam("csearch") Long id) {

        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<NoteDTO> sq = noteService.getCustomerNotes(id, pageable);
        DataTablesOutput<NoteDTO> out = new DataTablesOutput<NoteDTO>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

    @GetMapping("/{id}/notes/new")
    public String addCustomerNotes(@PathVariable Long id, RedirectAttributes redirectAttributes, Model model,HttpServletRequest httpServletRequest) {
        try {
            ManagedUser managedUser = userService.getUserModelById(id, httpServletRequest).getManagedUser();
            NoteDTO noteDTO = new NoteDTO();
            noteDTO.setCustomerId(managedUser.getId());
            model.addAttribute("noteDTO", noteDTO );
            return "customer/add-note";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer";
        }
    }

    @GetMapping("/new")
    public String addAdminUsers(Model model) {
        ManagedUser managedUser = new ManagedUser();
        managedUser.setUserCategory(UserCategory.CUSTOMER);
        model.addAttribute("managedUser", managedUser);
        return "customer/add";
    }

    @PostMapping("/notes")
    public String createCustomerNote(@ModelAttribute("noteDTO") @Valid NoteDTO noteDTO, BindingResult result, Principal principal, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "customer/add-note";
        }

        try {
            noteDTO.setCustomer(userService.getUserModelById(noteDTO.getCustomerId(), httpServletRequest).getManagedUser());
            noteDTO.setAdmin(userService.getCurrentLoggedInUser());
            noteDTO.setMadeOn(new Date());
            String message = noteService.createNote(noteDTO);
                redirectAttributes.addFlashAttribute("message", message);
                return "redirect:/app/customer/"+noteDTO.getCustomerId()+"/detail";
        } catch (PaaroException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());
            logger.error("Error creating content", ibe);
            return "customer/add-note";
        }catch (Exception e) {
            result.addError(new ObjectError("error", e.getMessage()));
            model.addAttribute("failure", e.getMessage());
            logger.error("Error creating content", e);
            return "customer/add-note";
        }
    }

    @PostMapping
    public String createAdminUsers(@Valid ManagedUserModelApi managedUserModelApi, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "customer/add";
        }

        try {
            ManagedUserModelApi message = userService.createAdminUser(managedUserModelApi, httpServletRequest);
            if (message.getResponseStatus().equals(ResponseStatus.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", message.getMessage());
                return "redirect:/app/customer";
            } else {
                model.addAttribute("managedUser", managedUserModelApi);
                model.addAttribute("failure", message.getMessage());
                logger.error("Error creating content", message.getMessage());
                return "customer/add";
            }
        } catch (PaaroException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            model.addAttribute("managedUser", managedUserModelApi);
            model.addAttribute("failure", ibe.getMessage());
            logger.error("Error creating content", ibe);
            return "customer/add";
        }
    }

    @GetMapping("/{id}/edit")
    public String editFaq(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            model.addAttribute("managedUser", userService.getUserModelById(id, httpServletRequest).getManagedUser());
            return "customer/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer";
        }

    }

    @GetMapping("/{id}/detail")
    public String customerDetail(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            ManagedUser managedUser = userService.getUserModelById(id, httpServletRequest).getManagedUser();
            model.addAttribute("managedUser", managedUser);
            model.addAttribute("ngnWallet", walletService.findByManagedUser_EmailAndCurrency_Type(managedUser.getEmail(), "NGN"));
            model.addAttribute("gbpWallet", walletService.findByManagedUser_EmailAndCurrency_Type(managedUser.getEmail(), "GBP"));
            model.addAttribute("customerStat", transactionSearchService.fetchOngoingTransactionDetailsByPage(id));
            return "customer/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer";
        }

    }

    @GetMapping("/{id}/{currency}/kyc")
    public String customerWalletKyc(@PathVariable Long id, @PathVariable String currency, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            ManagedUser managedUser = userService.getUserModelById(id, httpServletRequest).getManagedUser();
            model.addAttribute("managedUser", managedUser);
            model.addAttribute("wallet", walletService.getUserWalletByCurrencyType(managedUser, currency));
            model.addAttribute("kyc", kycService.getKycByCurrencyAndUser(currency, managedUser));
            return "customer/kyc";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+id+"/detail";
        }

    }

    @GetMapping("/{id}/{clientId}/verify/passport")
    public String verifyPassportKyc(@PathVariable Long id,@PathVariable Long clientId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            KycResponseModel kycResponseModel = kycService.verifyPassportKyc(id, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", kycResponseModel.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/decline/passport")
    public String declinePassportKyc(@PathVariable Long id, @PathVariable Long clientId,HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            KycResponseModel kycResponseModel = kycService.declinePassportKyc(id, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", kycResponseModel.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/download/passport")
    public String downloadPassportKyc(@PathVariable Long id, @PathVariable Long clientId, HttpServletResponse httpServletResponse, RedirectAttributes redirectAttributes, Model model) {
        try {
            kycService.downloadKycAsAttachment(httpServletResponse,id, "PASSPORT_PHOTOGRAPH");
            redirectAttributes.addFlashAttribute("message", "Operation Successful");
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/verify/valid-id")
    public String verifyValidIdKyc(@PathVariable Long id,@PathVariable Long clientId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            KycResponseModel kycResponseModel = kycService.verifyValidIdKyc(id, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", kycResponseModel.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/decline/valid-id")
    public String declineValidKyc(@PathVariable Long id,@PathVariable Long clientId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            KycResponseModel kycResponseModel = kycService.declineValidIdKyc(id, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", kycResponseModel.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/download/valid-id")
    public String downloadValidIdKyc(@PathVariable Long id, @PathVariable Long clientId, HttpServletResponse httpServletResponse, RedirectAttributes redirectAttributes, Model model) {
        try {
            kycService.downloadKycAsAttachment(httpServletResponse,id, "VALID_ID");
            redirectAttributes.addFlashAttribute("message", "Operation Successful");
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/verify/utility")
    public String verifyUtilityKyc(@PathVariable Long id,@PathVariable Long clientId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            KycResponseModel kycResponseModel = kycService.verifyUtilityBillKyc(id, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", kycResponseModel.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/decline/utility")
    public String declineUtilityKyc(@PathVariable Long id,@PathVariable Long clientId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            KycResponseModel kycResponseModel = kycService.declineUtilityBillKyc(id, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", kycResponseModel.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/download/utility")
    public String downloadUtilityKyc(@PathVariable Long id, @PathVariable Long clientId, HttpServletResponse httpServletResponse, RedirectAttributes redirectAttributes, Model model) {
        try {
            kycService.downloadKycAsAttachment(httpServletResponse,id, "UTILITY_BILL");
            redirectAttributes.addFlashAttribute("message", "Operation Successful");
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/verify/bvn")
    public String verifyBvnKyc(@PathVariable Long id,@PathVariable Long clientId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            KycResponseModel kycResponseModel = kycService.verifyBvnKyc(id, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", kycResponseModel.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @GetMapping("/{id}/{clientId}/decline/bvn")
    public String declineBvnKyc(@PathVariable Long id,@PathVariable Long clientId, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            KycResponseModel kycResponseModel = kycService.declineBvnKyc(id, httpServletRequest);
            redirectAttributes.addFlashAttribute("message", kycResponseModel.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer/"+ clientId+"/detail";
        }
    }

    @PostMapping("/update")
    public String updateBank(@Valid ManagedUserModelApi managedUser, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Locale locale) {
        if (result.hasErrors()) {
            result.addError(new ObjectError("invalid", messageSource.getMessage("form.fields.required", null, locale)));
            return "customer/edit";
        }

        try {

            ManagedUserModelApi message = userService.updateCustomerUser(managedUser, httpServletRequest);
            if (message.getResponseStatus().equals(ResponseStatus.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", message.getMessage());
                redirectAttributes.addAttribute("customerStat", transactionSearchService.fetchOngoingTransactionDetailsByPage(managedUser.getId()));
                return "redirect:/app/customer/"+ managedUser.getId() +"detail";
            } else {
                model.addAttribute("managedUser", managedUser);
                model.addAttribute("failure", message.getMessage());
                logger.error("Error creating content", message.getMessage());
                return "customer/edit";
            }

        } catch (PaaroException ibe) {
            result.addError(new ObjectError("error", ibe.getMessage()));
            model.addAttribute("failure", ibe.getMessage());
            model.addAttribute("managedUser", managedUser);
            logger.error("Error creating content", ibe);
            return "customer/edit";
        }
    }

    @GetMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            ManagedUser user = userService.getUserModelById(id, httpServletRequest).getManagedUser();
            ManagedUserModelApi managedUserModelApi = new ManagedUserModelApi();
            if (user.isActive()) {
                managedUserModelApi = userService.deactivateUser(user.getEmail(), httpServletRequest);
            } else {
                managedUserModelApi = userService.activateUser(user.getEmail(), httpServletRequest);
            }
            redirectAttributes.addFlashAttribute("message", managedUserModelApi.getMessage());
            return "redirect:/app/customer";
        } catch (Exception e) {
            model.addAttribute("failure", e.getMessage());
            return "customer/view";
        }
    }

    @GetMapping("/{id}/enable")
    public String enableCurrency(@PathVariable Long id, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            ManagedUser user = userService.getUserModelById(id, httpServletRequest).getManagedUser();
            ManagedUserModelApi managedUserModelApi = userService.activateUser(user.getEmail(), httpServletRequest);
            redirectAttributes.addFlashAttribute("message", managedUserModelApi.getMessage());
            return "redirect:/app/customer/"+ id +"detail";
        } catch (Exception e) {
            model.addAttribute("failure", e.getMessage());
            return "customer/view";
        }
    }

    @GetMapping("/{id}/disable")
    public String disableCurrency(@PathVariable Long id, HttpServletRequest httpServletRequest, RedirectAttributes redirectAttributes, Model model) {
        try {
            ManagedUser user = userService.getUserModelById(id, httpServletRequest).getManagedUser();
            ManagedUserModelApi managedUserModelApi = userService.deactivateUser(user.getEmail(), httpServletRequest);
            return "redirect:/app/customer/"+ id +"detail";
        } catch (Exception e) {
            model.addAttribute("failure", e.getMessage());
            return "customer/view";
        }
    }

    @GetMapping("/{id}/authority")
    public String manageAuthorities(@PathVariable Long id, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest, Model model) {
        try {
            ManagedUserModelApi managedUser = userService.getUserModelById(id, httpServletRequest);
            model.addAttribute("managedUser", managedUser.getManagedUser());
            model.addAttribute("userAuthorities", managedUser.getAuthorityList());
            model.addAttribute("nonUserAuthorities", authorityService.fetchUnMappedAuthoritiesForUser(managedUser.getManagedUser().getId()).getAuthorityList());
            model.addAttribute("authorityList", authorityService.getAllAuthorities(httpServletRequest));
            return "customer/authority";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer";
        }

    }

    @PostMapping("/authority")
    public String mapAuthorities(ManagedUser managedUser, RedirectAttributes redirectAttributes, Model model, WebRequest request, HttpServletRequest httpServletRequest, Locale locale) {

        try {
            ManagedUserModelApi managedUserModelApi = userService.getUserModelById(managedUser.getId(), httpServletRequest);
            logger.info("Auth {}", managedUserModelApi.toString());
            List<String> authorityList = new ArrayList<>();

            String[] permissions = request.getParameterValues("permissionsList");
            if (permissions != null) {
                for (String perm : permissions) {
                    authorityList.add(perm);
                }
            }
            ManagedUserModelApi response = authorityService.mapAuthoritiesToUser(managedUserModelApi.getManagedUser().getEmail(), authorityList, httpServletRequest);
            if (response.getResponseStatus().equals(ResponseStatus.SUCCESSFUL)) {
                redirectAttributes.addFlashAttribute("message", response.getMessage());
                return "redirect:/app/customer";
            } else {
                model.addAttribute("managedUser", managedUserModelApi.getManagedUser());
                model.addAttribute("userAuthorities", managedUserModelApi.getAuthorityList());
                model.addAttribute("nonUserAuthorities", authorityService.fetchUnMappedAuthoritiesForUser(managedUserModelApi.getManagedUser().getId()).getAuthorityList());
                model.addAttribute("authorityList", authorityService.getAllAuthorities(httpServletRequest));
                model.addAttribute("failure", response.getMessage());
                logger.error("Error creating content", response.getMessage());
                return "customer/authority";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("failure", e.getMessage());
            return "redirect:/app/customer";
        }
    }
}
