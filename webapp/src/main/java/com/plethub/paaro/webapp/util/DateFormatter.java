package com.plethub.paaro.webapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    static SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    static SimpleDateFormat switchDateFormatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    static SimpleDateFormat rateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    public static String format(Date date){
        return dateFormatter.format(date);
    }

    public static String switchFormat(Date date){
        return switchDateFormatter.format(date);
    }

    public static String rateFormat(Date date){
        return rateFormatter.format(date);
    }

    public static Date formatString(String date){
        try {
            return rateFormatter.parse(date);
        }catch (Exception e){
            return null;
        }

    }

}




