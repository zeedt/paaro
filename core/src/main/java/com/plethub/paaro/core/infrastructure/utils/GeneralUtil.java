package com.plethub.paaro.core.infrastructure.utils;

import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GeneralUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(GeneralUtil.class.getName());

    public static final String VALID_ID_KYC = "VALID_ID";

    public static final String PASSPORT_PHOTOGRAPH_KYC = "PASSPORT_PHOTOGRAPH";

    public static final String UTILITY_BILL_KYC = "UTILITY_BILL";

    public static String generateRandomValueForRequest() {
        Long currentTimeMillis = System.currentTimeMillis();
        SecureRandom secureRandom = new SecureRandom();
        int randomNumber = secureRandom.nextInt(1001);
        String randomString = String.format("%04d",randomNumber);
        randomString = currentTimeMillis.toString() + randomString;
        return randomString;
    }

    public static String generateTokenForRequest() {
        SecureRandom secureRandom = new SecureRandom();
        int randomNumber = secureRandom.nextInt(100001);
        String token = String.valueOf(randomNumber);
        LOGGER.info("Token generated is " + token);
        return token;
    }

    public static List<TransactionStatus> getUnmatchedStatuses() {
        List<TransactionStatus> transactionStatuses = new ArrayList<>();

        transactionStatuses.add(TransactionStatus.CUSTOMER_LOGGED_REQUEST);
        transactionStatuses.add(TransactionStatus.ADMIN_VERIFIED_LOGGED_REQUEST);

        return transactionStatuses;
    }


    public static Date getStartOfCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();
    }

    public static Date getStartOfCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();
    }

    public static Date getStartOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();
    }

    public static Date getStartOfCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTime();
    }

}
