package com.plethub.paaro.webapp.service;

import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.webapp.ApiModel.UserDetails;
import com.plethub.paaro.webapp.dto.ChangePassword;
import com.plethub.paaro.webapp.dto.ForgotPassword;
import com.plethub.paaro.webapp.dto.LoginForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface BankService {

    Page<Bank> getLocalBanks(Pageable pageDetails);

    Page<Bank> getAllBanks(Pageable pageDetails);

    Page<Bank> getForeignBanks(Pageable pageDetails);

    Bank getBankDetails(Long id);
}
