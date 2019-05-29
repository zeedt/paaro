package com.plethub.paaro.core.appservice.services;

import com.plethub.paaro.core.models.Currency;
import com.plethub.paaro.core.appservice.repository.CurrencyRepository;
import com.plethub.paaro.core.models.ManagedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class WalletBackgroundService {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private WalletService walletService;

    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    private Logger logger = LoggerFactory.getLogger(WalletBackgroundService.class.getName());

    public void createAllCurrencyWalletsForCustomer(ManagedUser managedUser) {

        executorService.submit( () -> {

            List<Currency>  currencies = currencyRepository.findAll();

            if (CollectionUtils.isEmpty(currencies)) {
                return;
            }

            currencies.stream().forEach(currency -> {
                try {

                    if (currency !=  null) {
                        walletService.addWalletToUserAccount(currency,managedUser);
                    }

                } catch (Exception e) {
                    logger.error("Error occurred while adding currency to user account due to ", e);
                }
            });

        });

    }

}
