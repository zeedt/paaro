package com.plethub.paaro.core.infrastructure.utils;

import org.springframework.stereotype.Service;

@Service
public class PaymentReferenceGenerationStrategy {

    public  String incrementPaymentRefrenceId(String current) throws Exception {
        String convertChar = current.toUpperCase();
        Details details = new Details();
        details.processed = false;
        details.current = current;
        details.chars = convertChar.toCharArray();
        //Considering the first character
        processFirstCharacter(details);
        //Considering the second character
        processSecondCharacter(details,convertChar);
        //Considering the third character
        processThirdCharacter(details,convertChar);
        //Considering the fourth character
        processFourthCharacter(details,convertChar);
        //Considering the fifth character
        processFifthCharacter(details,convertChar);
        String returnedString = "";
        for (char aChar : details.chars) {
            returnedString = returnedString + aChar;
        }
        return returnedString;
    }

    public  void processFirstCharacter(Details details){
        if(Character.isDigit(details.chars[0])){
            if (details.current.substring(0).equals("99999")){
                details.chars[0] = 'A'; details.chars[1]='0';details.chars[2]='0'; details.chars[3]='0';details.chars[4]='0';
                details.processed = true;
            }else{
                details.current = String.format("%05d",Integer.parseInt(details.current) + 1);
                details.chars = details.current.toCharArray();
                details.processed = true;
            }
        }
        else if(details.chars[0]<'Z'  && !details.processed){
            if (details.current.substring(1).equals("9999")){
                details.chars[0] = ++details.chars[0]; details.chars[1]='0';details.chars[2]='0';details.chars[3]='0';details.chars[4]='0';
                details.processed = true;
            }else{
                String newCurrent = String.format("%05d",Integer.parseInt(details.current.substring(1)) + 1);
                details.chars[1] = newCurrent.charAt(1);details.chars[2] = newCurrent.charAt(2);details.chars[3] = newCurrent.charAt(3);details.chars[4] = newCurrent.charAt(4);
                details.processed = true;
            }
        }
    }

    public void processSecondCharacter(Details details, String convertChar){
        if(Character.isDigit(details.chars[1]) && convertChar.charAt(0)=='Z' && !details.processed ){
            if (details.current.substring(1).equals("9999")){
                details.chars[1] = 'A'; details.chars[2]='0';details.chars[3]='0';details.chars[4]='0';
                details.processed = true;
            }else{
                details.current = String.format("%05d",Integer.parseInt(details.current.substring(1)) + 1);
                details.chars = details.current.toCharArray(); details.chars[0]='Z';
                details.processed = true;
            }
        }else if(details.chars[1]<'Z' && details.chars[0]=='Z' && !details.processed){
            if (details.current.substring(2).equals("999")){
                details.chars[1] = ++details.chars[1]; details.chars[2]='0';details.chars[3]='0';details.chars[4]='0';
                details.processed = true;
            }else{
                String newCurrent = String.format("%05d",Integer.parseInt(details.current.substring(2)) + 1);
                details.chars[2] = newCurrent.charAt(2); details.chars[3] = newCurrent.charAt(3); details.chars[4] = newCurrent.charAt(4);
                details.processed = true;
            }
        }
    }

    public void processThirdCharacter(Details details, String convertChar){
        if(Character.isDigit(details.chars[2]) && convertChar.charAt(0)=='Z'  && convertChar.charAt(1)=='Z' && !details.processed ){
            if (details.current.substring(2).equals("999")){
                details.chars[2] = 'A'; details.chars[3]='0';details.chars[4]='0';
                details.processed = true;
            }else{
                details.current = String.format("%05d",Integer.parseInt(details.current.substring(2)) + 1);
                details.chars = details.current.toCharArray(); details.chars[0]='Z'; details.chars[1]='Z';
                details.processed = true;
            }
        }else if(details.chars[2]<'Z' && convertChar.charAt(0)=='Z' && convertChar.charAt(1)=='Z'  && !details.processed){
            if (details.current.substring(3).equals("99")){
                details.chars[2] = ++details.chars[2]; details.chars[3]='0'; details.chars[4]='0';
                details.processed = true;
            }else{
                String newCurrent = String.format("%05d",Integer.parseInt(details.current.substring(3)) + 1);
                details.chars[3] = newCurrent.charAt(3); details.chars[4] = newCurrent.charAt(4);
                details.processed = true;
            }
        }
    }

    public  void processFourthCharacter(Details details, String convertChar)  {
        if(Character.isDigit(details.chars[3]) && convertChar.charAt(0)=='Z'  && convertChar.charAt(1)=='Z'   && convertChar.charAt(2)=='Z' && !details.processed ){
            if (details.current.substring(3).equals("99")){
                details.chars[3]='A';details.chars[4]='0';
                details.processed = true;
            }else{
                details.current = String.format("%05d",Integer.parseInt(details.current.substring(3)) + 1);
                details.chars = details.current.toCharArray(); details.chars[0]='Z'; details.chars[1]='Z'; details.chars[2] = 'Z';
                details.processed = true;
            }
        }else if(details.chars[3]<'Z' && convertChar.charAt(0)=='Z' && convertChar.charAt(1)=='Z' && convertChar.charAt(2)=='Z' && !details.processed){
            if (details.current.substring(4).equals("9")){
                details.chars[3] = ++details.chars[3]; details.chars[4]='0';
                details.processed = true;
            }else{
                String newCurrent = String.format("%05d",Integer.parseInt(details.current.substring(3)) + 1);
                details.chars[4] = newCurrent.charAt(4);
                details.processed = true;
            }
        }
    }
    public  void processFifthCharacter(Details details, String convertChar) throws Exception {
        if(Character.isDigit(details.chars[4]) && convertChar.charAt(0)=='Z'  && convertChar.charAt(1)=='Z' && convertChar.charAt(2)=='Z' && convertChar.charAt(3)=='Z' && !details.processed ){
            if (details.current.substring(4).equals("9")){
                details.chars[4]='A';
                details.processed = true;
            }else{
                details.current = String.format("%05d",Integer.parseInt(details.current.substring(4)) + 1);
                details.chars = details.current.toCharArray(); details.chars[0]='Z'; details.chars[1]='Z'; details.chars[2] = 'Z';details.chars[3] = 'Z';
                details.processed = true;
            }
        }else if(details.chars[4]<'Z' && convertChar.charAt(0)=='Z' && convertChar.charAt(1)=='Z' && convertChar.charAt(2)=='Z' && convertChar.charAt(3)=='Z' && !details.processed){
            if (details.chars[4]<'Z'){
                details.chars[4] = ++details.chars[4];
                details.processed = true;
            }
        }else if((Character.isAlphabetic(details.chars[4]) && details.chars[4] >= 'Z') || !details.processed){
            throw new Exception("Unable to generate next number. Last passed is " + convertChar);
        }
    }

    class Details {
        public String current;
        public boolean processed = false;
        public char[] chars;
    }

}
