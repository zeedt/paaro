package com.plethub.paaro.core.appservice.services;

import com.plethub.paaro.core.appservice.apirequestmodel.TransferTransactionSearchRequestModel;
import com.plethub.paaro.core.appservice.repository.WalletTransferTransactionRepository;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.usermanagement.service.AuditLogService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionDownloadService {

    private Logger logger = LoggerFactory.getLogger(TransactionDownloadService.class.getName());

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private TransactionSearchService searchService;

    @Autowired
    private WalletTransferTransactionRepository walletTransferTransactionRepository;

    public void generateExcelForTransactions(TransferTransactionSearchRequestModel requestModel, HttpServletResponse httpServletResponse, HttpServletRequest servletRequest) throws IOException {

        List<WalletTransferTransaction> walletTransferTransactions = searchTransactionsByAndOptions(requestModel);

        logger.info("Transactions fetched is " + walletTransferTransactions.size());

        String fileName = "Transaction report.xlsx";
        httpServletResponse.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=\"" + fileName + "\"";
        httpServletResponse.setHeader(headerKey, headerValue);

        Workbook workbook = new XSSFWorkbook();

        workbook.createSheet("Transactions");

        workbook = generateHeaderForExcel(workbook);

        workbook = writeTransactionsToExcel(workbook, walletTransferTransactions);

        workbook.write(httpServletResponse.getOutputStream());

        workbook.close();

        httpServletResponse.flushBuffer();

        auditLogService.saveAudit(null, null, Module.REPORT,servletRequest,"User downloaded transfer transaction report");

    }

    public Workbook generateHeaderForExcel(Workbook workbook) {

        workbook.getSheetAt(0);
        String[] excelHeader = new String[]{"Currency(from)","Currency(to)","Actual Amount","Charge Amount","Total Amount","Exchange Rate","Account Name","System ref Id","TP ref Id","Narration","Status","Date initiated", "Settled", "Recipient Account Number", "Customer name"};
        Sheet sheet = workbook.getSheet("Transactions");
        Row row = sheet.createRow(0);

        for (int i=0;i<excelHeader.length;i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
        }

        return workbook;

    }

    public Workbook writeTransactionsToExcel(Workbook workbook, List<WalletTransferTransaction> transferTransactions) {

        Sheet sheet = workbook.getSheet("Transactions");

        int rowNum = 1;

        Row row;
        Cell cell;

        for (WalletTransferTransaction transferTransaction : transferTransactions) {
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue(transferTransaction.getFromCurrency().getDescription());

            cell = row.createCell(1);
            cell.setCellValue(transferTransaction.getToCurrency().getDescription());

            cell = row.createCell(2);
            cell.setCellValue(String.valueOf(transferTransaction.getActualAmount()));

            cell = row.createCell(3);
            cell.setCellValue(String.valueOf(transferTransaction.getChargeAmount()));

            cell = row.createCell(4);
            cell.setCellValue(String.valueOf(transferTransaction.getTotalAmount()));

            cell = row.createCell(5);
            cell.setCellValue(String.valueOf(transferTransaction.getExchangeRate()));

            cell = row.createCell(6);
            cell.setCellValue(transferTransaction.getToAccountName());

            cell = row.createCell(7);
            cell.setCellValue(transferTransaction.getPaaroReferenceId());

            cell = row.createCell(8);
            cell.setCellValue(transferTransaction.getThirdPartyReferenceId());

            cell = row.createCell(9);
            cell.setCellValue(transferTransaction.getNarration());

            cell = row.createCell(10);
            cell.setCellValue(transferTransaction.getTransactionStatus().toString());

            cell = row.createCell(11);
            cell.setCellValue(transferTransaction.getInitiatedDate());

            cell = row.createCell(12);
            String settledValue = (transferTransaction.getSettled()) ? "Y" : "N";
            cell.setCellValue(settledValue);

            cell = row.createCell(13);
            cell.setCellValue(transferTransaction.getToAccountNumber());

            cell = row.createCell(14);
            if (transferTransaction.getManagedUser() != null) {
                String customerName = transferTransaction.getManagedUser().getFirstName() + " " + transferTransaction.getManagedUser().getLastName();
                cell.setCellValue(customerName);
            } else {
                cell.setCellValue("N/A");
            }

        }

        return workbook;
    }


    public List searchTransactionsByAndOptions(TransferTransactionSearchRequestModel requestModel) {

        if (requestModel == null) return new ArrayList<>();

        return walletTransferTransactionRepository.findAll((Specification<WalletTransferTransaction>) (root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.and(getSearchPredicateFromRequest(requestModel, criteriaBuilder, root).toArray(new Predicate[0]));
        });

    }

    private List<Predicate> getSearchPredicateFromRequest(TransferTransactionSearchRequestModel requestModel, CriteriaBuilder criteriaBuilder, Root root) {

        List<Predicate> searchPredicate = new ArrayList<>();

        if (requestModel.getTransactionStatus() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("transactionStatus")), requestModel.getTransactionStatus()));
        if (requestModel.getId() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("id")), requestModel.getId()));
        if (!StringUtils.isEmpty(requestModel.getAccountName()))
            searchPredicate.add(criteriaBuilder.like((root.get("toAccountName")), "%"+requestModel.getAccountName()+"%"));
        if (!StringUtils.isEmpty(requestModel.getAccountNumber()))
            searchPredicate.add(criteriaBuilder.like((root.get("toAccountNumber")), "%"+requestModel.getAccountNumber()+"%"));
        if (!StringUtils.isEmpty(requestModel.getPaaroReferenceId()))
            searchPredicate.add(criteriaBuilder.like((root.get("paaroReferenceId")), "%"+requestModel.getPaaroReferenceId()+"%"));
        if (!StringUtils.isEmpty(requestModel.getFromCurrency()))
            searchPredicate.add(criteriaBuilder.like((root.get("fromCurrency").get("type")), "%"+requestModel.getFromCurrency()+"%"));
        if (!StringUtils.isEmpty(requestModel.getToCurrency()))
            searchPredicate.add(criteriaBuilder.like((root.get("toCurrency").get("type")), "%"+requestModel.getToCurrency()+"%"));
        if (requestModel.getUserId() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("managedUser").get("id")), requestModel.getUserId()));
        if (requestModel.getSettled() != null)
            searchPredicate.add(criteriaBuilder.equal((root.get("isSettled")), requestModel.getSettled()));
        if (!CollectionUtils.isEmpty(requestModel.getStatuses()))
            searchPredicate.add((root.get("transactionStatus").in(requestModel.getStatuses())));
        if (requestModel.getFromDate() != null && requestModel.getToDate() != null)
            searchPredicate.add(criteriaBuilder.between((root.get("initiatedDate")), requestModel.getFromDate(), requestModel.getToDate()));

        return searchPredicate;

    }



}
