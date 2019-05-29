package com.plethub.paaro.core.appservice.impl;

import com.plethub.paaro.core.appservice.dao.SettleForeignAccount;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import com.plethub.paaro.core.appservice.thirdparty.models.flutterwave.FlutterWaveTransferDataResponse;
import com.plethub.paaro.core.appservice.thirdparty.models.flutterwave.FlutterWaveTransferRequest;
import com.plethub.paaro.core.appservice.thirdparty.models.flutterwave.FlutterWaveTransferResponse;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class SettleForeignAccountService implements SettleForeignAccount {

    private RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory());

    @Value("${flutter-wave.account.validation:https://ravesandboxapi.flutterwave.com/v2/gpx/transfers/create}")
    private String transferEndpoint;

    @Value("${flutter-wave.api.security-key:FLWPUBK-ed439274dc425f4f86c08d9c7627da26-X}")
    private String securityKey;

    @Value("${currency.naira-type:NGN}")
    private String nairaCurrency;

    private static final String NARRATION = "Transfer to account";

    private Logger logger = LoggerFactory.getLogger(SettleForeignAccountService.class.getName());

    @Override
    public void settleForeignAccount(WalletTransferTransaction walletTransferTransaction) {

        Integer noOftrials = (walletTransferTransaction.getNoOfPaymentTrial() == null) ? 0 : walletTransferTransaction.getNoOfPaymentTrial();

        FlutterWaveTransferRequest flutterWaveTransferRequest = createFlutterwaveTransferRequestFromTransaction(walletTransferTransaction);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        try {
            FlutterWaveTransferResponse flutterWaveTransferResponse = restTemplate.postForObject(transferEndpoint, flutterWaveTransferRequest, FlutterWaveTransferResponse.class, httpHeaders);

            if (flutterWaveTransferResponse == null) {
                walletTransferTransaction.setErrorMessage("Null response received from API");
            }

            FlutterWaveTransferDataResponse transferDataResponse = flutterWaveTransferResponse.getData();

            if (transferDataResponse == null) {
                walletTransferTransaction.setErrorMessage(flutterWaveTransferResponse.getMessage());
            }
            else if ("FAILED".equals(transferDataResponse.getStatus())) {
                walletTransferTransaction.setErrorMessage(transferDataResponse.getCompleteMessage());
            } else {

            }

        } catch (HttpStatusCodeException e) {
            logger.error(String.format("Error occurred while transferring for paaro refrence id %s  due to ",walletTransferTransaction.getPaaroReferenceId()), e);
        } catch (Exception e) {
            logger.error(String.format("Error occurred while transferring for paaro refrence id %s  due to ",walletTransferTransaction.getPaaroReferenceId()), e);
        }

        noOftrials += 1;


    }

    private FlutterWaveTransferRequest createFlutterwaveTransferRequestFromTransaction(WalletTransferTransaction walletTransferTransaction) {

        FlutterWaveTransferRequest flutterWaveTransferRequest = new FlutterWaveTransferRequest();

        flutterWaveTransferRequest.setAccountBank(walletTransferTransaction.getToAccountNumber());
        flutterWaveTransferRequest.setAmount(walletTransferTransaction.getActualAmount());
        flutterWaveTransferRequest.setCurrency(nairaCurrency);
        flutterWaveTransferRequest.setSecurityKey(securityKey);
        flutterWaveTransferRequest.setNarration(String.format("%s - %s", NARRATION, walletTransferTransaction.getToAccountNumber()));
        flutterWaveTransferRequest.setAccountBank(walletTransferTransaction.getReceivingBank().getBankCode());
        flutterWaveTransferRequest.setReference(GeneralUtil.generateRandomValueForRequest());

        return flutterWaveTransferRequest;
    }
}
