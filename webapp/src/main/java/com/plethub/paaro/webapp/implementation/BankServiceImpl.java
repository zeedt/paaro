package com.plethub.paaro.webapp.implementation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.plethub.paaro.core.appservice.enums.BankType;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.webapp.ApiModel.BankResponse;
import com.plethub.paaro.webapp.ApiModel.UserDetails;
import com.plethub.paaro.webapp.exception.PaaroException;
import com.plethub.paaro.webapp.service.BankService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankServiceImpl implements BankService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BankRepository bankRepository;

    @Override
    public Page<Bank> getLocalBanks(Pageable pageDetails) {
        Page<Bank> page =  bankRepository.findAllByBankType(BankType.LOCAL, pageDetails);
        return page;
    }

    @Override
    public Page<Bank> getAllBanks(Pageable pageDetails) {
        Page<Bank> page =  bankRepository.findAll(pageDetails);
        return page;
    }

    @Override
    public Page<Bank> getForeignBanks(Pageable pageDetails) {
        Page<Bank> page =  bankRepository.findAllByBankType(BankType.FOREIGN, pageDetails);
        return page;
    }

    @Override
    public Bank getBankDetails(Long id) {
        Bank bank = bankRepository.getOne(id);
        return bank;
    }


}
