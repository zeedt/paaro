package com.plethub.paaro.core.usermanagement.service;

import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.models.AuditLog;
import com.plethub.paaro.core.usermanagement.repository.AuditLogRepository;
//import com.plethub.paaro.core.security.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.infrastructure.utils.DateUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class AuditLogService {

    private ExecutorService executorService = Executors.newFixedThreadPool(1);

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private AuditLogService auditLogService;

    private Logger logger = LoggerFactory.getLogger(AuditLogService.class.getName());


    @Autowired
    private AuditLogRepository auditLogRepository;

    public void saveAudit(String initialData, String finalData, Module module, HttpServletRequest servletRequest, String action) {
        UserDetailsTokenEnvelope userDetailsTokenEnvelope =  null;

        try {
            userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {

        }
        String email = "";
        if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
            email = userDetailsTokenEnvelope.getManagedUser().getEmail();
        }
        String finalEmail = email;
        String ipAddress = servletRequest.getRemoteAddr();
        executorService.submit(()->{
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setInitialData(initialData);
            auditLog.setFinalData(finalData);
            auditLog.setDatePerformed(new Date());
            auditLog.setFinalData(finalData);
            auditLog.setIpAddress(ipAddress);
            auditLog.setPerformedBy(finalEmail);
            auditLog.setModule(module);

            auditLogRepository.save(auditLog);
        });

    }
    public void saveAudit(String initialData, String finalData, Module module, HttpServletRequest servletRequest, String action, Long entityId) {
        UserDetailsTokenEnvelope userDetailsTokenEnvelope =  null;

        try {
            userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String email = "";
        if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
            email = userDetailsTokenEnvelope.getManagedUser().getEmail();
        }
        String finalEmail = email;
        String ipAddress = servletRequest.getRemoteAddr();
        executorService.submit(()->{
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setInitialData(initialData);
            auditLog.setFinalData(finalData);
            auditLog.setDatePerformed(new Date());
            auditLog.setFinalData(finalData);
            auditLog.setIpAddress(ipAddress);
            auditLog.setPerformedBy(finalEmail);
            auditLog.setModule(module);
            auditLog.setEntityId(entityId);

            auditLogRepository.save(auditLog);
        });

    }

    public void saveAudit(String initialData, String finalData, Module module, String email, String action, Long entityId) {

        executorService.submit(()->{
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setInitialData(initialData);
            auditLog.setFinalData(finalData);
            auditLog.setDatePerformed(new Date());
            auditLog.setFinalData(finalData);
            auditLog.setIpAddress("");
            auditLog.setPerformedBy(email);
            auditLog.setModule(module);
            auditLog.setEntityId(entityId);

            auditLogRepository.save(auditLog);
        });

    }

    public AuditLog getLastAuditedByModule(Module module) {

        AuditLog auditLog = auditLogRepository.findTopByModuleIsOrderByIdDesc(module);

        return auditLog;

    }

    public AuditLog getById(Long id) {
        return auditLogRepository.findOne(id);
    }

    public Page<AuditLog> getAuditLogs(Pageable pageable) {

        return auditLogRepository.findAll(pageable);

    }

    public Page<AuditLog> getAuditLogWithFilter(String filter, int pageNo, int pageSize, String fromDateStr, String toDateStr) {

        if (filter == null) {
            filter = "";
        }

        Date toDate = null;
        Date fromDate = null;

        try {
            toDate = dateUtils.convertStringToDate(toDateStr,"yyyy-MM-dd");
            fromDate = dateUtils.convertStringToDate(fromDateStr,"yyyy-MM-dd");
        } catch (Exception e) {
            logger.error("Unable to convert date string to date due to ", e);
        }

        PageRequest pageRequest = new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id");

        if (fromDate != null && toDate != null) {
            return auditLogRepository.findLogWithFilterPlusDateRange("%"+filter+"%",fromDate, toDate,pageRequest);
        }

        return auditLogRepository.findLogWithFilter("%"+filter+"%",pageRequest);
    }


    public List<AuditLog> getAuditLogListWithFilter(String filter, String fromDateStr, String toDateStr) {

        if (filter == null) {
            filter = "";
        }

        Date toDate = null;
        Date fromDate = null;

        try {
            toDate = dateUtils.convertStringToDate(toDateStr,"yyyy-MM-dd");
            fromDate = dateUtils.convertStringToDate(fromDateStr,"yyyy-MM-dd");
        } catch (Exception e) {
            logger.error("Unable to convert date string to date due to ", e);
        }
        if (fromDate != null && toDate != null) {
            return auditLogRepository.findLogWithFilterPlusDateRange("%"+filter+"%",fromDate, toDate);
        }

        return auditLogRepository.findLogWithFilter("%"+filter+"%");

    }


    public void generateExcelForAudit(String filter, HttpServletResponse httpServletResponse, String fromDateStr, String toDateStr, HttpServletRequest servletRequest) throws IOException {

        List<AuditLog> auditLogs = getAuditLogListWithFilter(filter, fromDateStr, toDateStr);

        logger.info("Logs fetched is " + auditLogs.size());

        Workbook workbook = new XSSFWorkbook();

        workbook.createSheet("Audit Log");

        workbook = generateHeaderForExcel(workbook);

        workbook = writeLogsToExcel(workbook, auditLogs);

        workbook.write(httpServletResponse.getOutputStream());

        workbook.close();

        httpServletResponse.flushBuffer();

        auditLogService.saveAudit(null, null, Module.REPORT,servletRequest,"User downloaded Audit log report");

    }

    private Workbook writeLogsToExcel(Workbook workbook, List<AuditLog> auditLogs) {

        Sheet sheet = workbook.getSheet("Audit Log");

        int rowNum = 1;

        Row row;
        Cell cell;

        for (AuditLog auditLog : auditLogs) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(auditLog.getAction());

            cell = row.createCell(1);
            cell.setCellValue(auditLog.getIpAddress());

            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(auditLog.getModule()));

            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(auditLog.getPerformedBy()));


            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(auditLog.getInitialData()));

            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(auditLog.getFinalData()));

            cell = row.createCell(6);
            cell.setCellValue(auditLog.getDatePerformed());

        }

        return workbook;
    }

    public Workbook generateHeaderForExcel(Workbook workbook) {

        workbook.getSheetAt(0);
        String[] excelHeader = new String[]{"Action","IP Address","Module","Actor","Initial data","Final Data","Date performed"};
        Sheet sheet = workbook.getSheet("Audit Log");
        Row row = sheet.createRow(0);

        for (int i=0;i<excelHeader.length;i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
        }

        return workbook;

    }

    public void saveLoginAudit(String username, Module module, HttpServletRequest servletRequest) {

        String ipAddress = servletRequest.getRemoteAddr();
        executorService.submit(()->{
            AuditLog auditLog = new AuditLog();
            auditLog.setAction("User successfully login");
            auditLog.setInitialData(username);
            auditLog.setFinalData(username);
            auditLog.setDatePerformed(new Date());
            auditLog.setIpAddress(ipAddress);
            auditLog.setPerformedBy(username);
            auditLog.setModule(module);
            auditLog.setEntityId(null);

            auditLogRepository.save(auditLog);
        });

    }

}
