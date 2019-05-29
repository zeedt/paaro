package com.plethub.paaro.core.appservice.util;

import com.plethub.paaro.core.appservice.enums.BankType;
import com.plethub.paaro.core.models.Bank;
import com.plethub.paaro.core.appservice.repository.BankRepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Service
public class GeneralUtils {

    private Logger logger = LoggerFactory.getLogger(GeneralUtils.class.getName());

    @Autowired
    private BankRepository bankRepository;

    String value = "{\n" +
            "  \"status\": \"success\",\n" +
            "  \"message\": \"Banks\",\n" +
            "  \"data\": [\n" +
            "    {\n" +
            "      \"name\": \"ACCESS BANK NIGERIA\",\n" +
            "      \"code\": \"044\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ACCESS MOBILE\",\n" +
            "      \"code\": \"323\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"AFRIBANK NIGERIA PLC\",\n" +
            "      \"code\": \"014\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Aso Savings and Loans\",\n" +
            "      \"code\": \"401\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"DIAMOND BANK PLC\",\n" +
            "      \"code\": \"063\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Ecobank Mobile\",\n" +
            "      \"code\": \"307\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ECOBANK NIGERIA LIMITED\",\n" +
            "      \"code\": \"050\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ENTERPRISE BANK LIMITED\",\n" +
            "      \"code\": \"084\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"FBN MOBILE\",\n" +
            "      \"code\": \"309\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"FIDELITY BANK PLC\",\n" +
            "      \"code\": \"070\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"FIRST BANK PLC\",\n" +
            "      \"code\": \"011\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"FIRST CITY MONUMENT BANK PLC\",\n" +
            "      \"code\": \"214\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"GTBank Mobile Money\",\n" +
            "      \"code\": \"315\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"GTBANK PLC\",\n" +
            "      \"code\": \"058\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"HERITAGE BANK\",\n" +
            "      \"code\": \"030\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"KEYSTONE BANK PLC\",\n" +
            "      \"code\": \"082\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Parkway\",\n" +
            "      \"code\": \"311\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"PAYCOM\",\n" +
            "      \"code\": \"305\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"SKYE BANK PLC\",\n" +
            "      \"code\": \"076\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"STANBIC IBTC BANK PLC\",\n" +
            "      \"code\": \"221\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Stanbic Mobile\",\n" +
            "      \"code\": \"304\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"STANDARD CHARTERED BANK NIGERIA LIMITED\",\n" +
            "      \"code\": \"068\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"STERLING BANK PLC\",\n" +
            "      \"code\": \"232\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"UNION BANK OF NIGERIA PLC\",\n" +
            "      \"code\": \"032\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"UNITED BANK FOR AFRICA PLC\",\n" +
            "      \"code\": \"033\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"UNITY BANK PLC\",\n" +
            "      \"code\": \"215\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"WEMA BANK PLC\",\n" +
            "      \"code\": \"035\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ZENITH BANK PLC\",\n" +
            "      \"code\": \"057\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ZENITH Mobile\",\n" +
            "      \"code\": \"322\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Coronation Merchant Bank\",\n" +
            "      \"code\": \"559\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Prime Bank Limited\",\n" +
            "      \"code\": \"10\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Co-operative Bank of Kenya Limited\",\n" +
            "      \"code\": \"11\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"National Bank of Kenya Limited\",\n" +
            "      \"code\": \"12\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Oriental Commercial Bank Limited\",\n" +
            "      \"code\": \"14\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Citibank N.A.\",\n" +
            "      \"code\": \"16\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Middle East Bank Kenya Limited\",\n" +
            "      \"code\": \"18\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Consolidated Bank of Kenya Limited\",\n" +
            "      \"code\": \"23\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Trans-National Bank Limited\",\n" +
            "      \"code\": \"26\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Chase Bank Limited\",\n" +
            "      \"code\": \"30\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"CFC Stanbic Bank Kenya Limited\",\n" +
            "      \"code\": \"31\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"African Banking Corp. Bank Ltd\",\n" +
            "      \"code\": \"35\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Imperial Bank Limited\",\n" +
            "      \"code\": \"39\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"NIC Bank Limited\",\n" +
            "      \"code\": \"41\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Giro Commercial Bank Limited\",\n" +
            "      \"code\": \"42\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ECO Bank Limited\",\n" +
            "      \"code\": \"43\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Equatorial Commercial Bank Limited\",\n" +
            "      \"code\": \"49\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Paramount Universal Bank Limited\",\n" +
            "      \"code\": \"50\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Jamii Bora Bank\",\n" +
            "      \"code\": \"51\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Fina Bank Limited\",\n" +
            "      \"code\": \"53\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Victoria Commercial Bank Limited\",\n" +
            "      \"code\": \"54\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Guardian Bank Limited\",\n" +
            "      \"code\": \"55\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Investments n Mortgages Bank Limited\",\n" +
            "      \"code\": \"57\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Development Bank of Kenya Limited\",\n" +
            "      \"code\": \"59\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Housing Finance Bank\",\n" +
            "      \"code\": \"61\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Diamond Trust Bank Limited\",\n" +
            "      \"code\": \"63\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"K-Rep Bank Limited\",\n" +
            "      \"code\": \"66\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Equity Bank Limited\",\n" +
            "      \"code\": \"68\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"UBA Kenya Bank Ltd\",\n" +
            "      \"code\": \"76\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Kenya Commercial Bank Limited\",\n" +
            "      \"code\": \"01\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Commercial Bank of Africa Limited\",\n" +
            "      \"code\": \"07\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Habib Bank Limited\",\n" +
            "      \"code\": \"08\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Central Bank of Kenya\",\n" +
            "      \"code\": \"09\",\n" +
            "      \"country\": \"KE\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"STANDARD CHARTERED BANK\",\n" +
            "      \"code\": \"300302\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"GCB BANK LTD\",\n" +
            "      \"code\": \"300304\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"NATIONAL INVESTMENT BANK\",\n" +
            "      \"code\": \"300305\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"UNIVERSAL MERCHANT BANK\",\n" +
            "      \"code\": \"300309\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"HFC BANK\",\n" +
            "      \"code\": \"300310\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ZENITH BANK\",\n" +
            "      \"code\": \"300311\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ECOBANK\",\n" +
            "      \"code\": \"300312\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"CAL BANK\",\n" +
            "      \"code\": \"300313\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"STANBIC BANK\",\n" +
            "      \"code\": \"300318\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"GUARANTY TRUST BANK\",\n" +
            "      \"code\": \"300322\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"UNITED BANK FOR AFRICA\",\n" +
            "      \"code\": \"300325\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ACCESS BANK\",\n" +
            "      \"code\": \"300329\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ENERGY BANK\",\n" +
            "      \"code\": \"300330\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"ROYAL BANK\",\n" +
            "      \"code\": \"300331\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"FIRST NATIONAL BANK\",\n" +
            "      \"code\": \"300334\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"FIRST ALLIED SAVINGS AND LOANS\",\n" +
            "      \"code\": \"300351\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"CAPITAL BANK\",\n" +
            "      \"code\": \"300356\",\n" +
            "      \"country\": \"GH\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"FSDH Merchant Bank Limited\",\n" +
            "      \"code\": \"601\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"PARRALEX BANK\",\n" +
            "      \"code\": \"526\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Providus Bank\",\n" +
            "      \"code\": \"101\",\n" +
            "      \"country\": \"NG\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Fidelity Bank\",\n" +
            "      \"code\": \"300323\",\n" +
            "      \"country\": \"GH\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";


//    @PostConstruct
    public void init() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        BankMigrationClassHolder bankMigrationClassHolder = objectMapper.readValue(value,BankMigrationClassHolder.class);

        List<BankDetails> bankDetailsList = bankMigrationClassHolder.getData();

        bankDetailsList.stream().filter(bankDetails -> bankDetails != null).forEach(bankDetails -> {

            try {
                Bank bank = new Bank();
                bank.setBankCode(bankDetails.getCode());
                bank.setBankName(bankDetails.getName());
                bank.setCountryCode(bankDetails.getCountry());
                if ("NG".equals(bankDetails.getCountry())) {
                    bank.setBankType(BankType.LOCAL);
                } else {
                    bank.setBankType(BankType.FOREIGN);
                }
                bankRepository.save(bank);
            } catch (Exception e) {
                logger.error("Error occurred due to ", e);
            }
        });

    }

}
