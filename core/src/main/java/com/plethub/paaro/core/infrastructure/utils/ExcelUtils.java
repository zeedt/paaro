package com.plethub.paaro.core.infrastructure.utils;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@Service
public class ExcelUtils {

    public Workbook getWorkBook(InputStream inputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);
        return workbook;
    }

    public HashMap<String,Integer> extractHeaderWithRowNum(Workbook workbook, Integer headerRowNumber){

        if(headerRowNumber==null){
            headerRowNumber = 1;
        }
        Sheet sheet = workbook.getSheetAt(0);
        int count = sheet.getRow(headerRowNumber).getPhysicalNumberOfCells();
        HashMap<String,Integer> headers = new HashMap<>();
        for (int i=0;i<count;i++) {
            headers.put(sheet.getRow(0).getCell(i).getStringCellValue(),i);
        }
        return headers;
    }

    public int getRowCount(int sheetIndex, Workbook workbook){
        return workbook.getSheetAt(sheetIndex).getPhysicalNumberOfRows();
    }

    public int getColumnCount(int sheetIndex, Workbook workbook,Integer headerRowNumber){
        if(headerRowNumber==null){
            headerRowNumber = 1;
        }
        return workbook.getSheetAt(sheetIndex).getRow(headerRowNumber).getPhysicalNumberOfCells();
    }


}
