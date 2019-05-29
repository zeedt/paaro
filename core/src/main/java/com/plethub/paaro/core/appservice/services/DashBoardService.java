package com.plethub.paaro.core.appservice.services;

import com.plethub.paaro.core.appservice.apirequestmodel.WalletRequest;
import com.plethub.paaro.core.appservice.apiresponsemodel.DashboardResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.models.WalletTransferTransaction;
import com.plethub.paaro.core.appservice.repository.WalletTransferTransactionRepository;
import com.plethub.paaro.core.infrastructure.utils.GeneralUtil;
//import com.plethub.paaro.core.security.UserDetailsTokenEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class DashBoardService {


    @Autowired
    private WalletTransferTransactionRepository walletTransferTransactionRepository;

    Logger logger = LoggerFactory.getLogger(DashBoardService.class.getName());

    public DashboardResponseModel fetchDashboardDetails() {

        try {
            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long id = 0L;

            if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
                id = userDetailsTokenEnvelope.getManagedUser().getId();
            }

            Page<WalletTransferTransaction> ongoingTransferTransactionPage = walletTransferTransactionRepository.
                    findAllByTransactionStatusInAndManagedUser_Id(GeneralUtil.getUnmatchedStatuses(),id,new PageRequest(0,10, Sort.Direction.DESC, "id"));

            Page<WalletTransferTransaction> completedTransferTransactionPage = walletTransferTransactionRepository.
                    findAllByTransactionStatusInAndManagedUser_Id(Collections.singletonList(TransactionStatus.COMPLETED),id,new PageRequest(0,10, Sort.Direction.DESC, "id"));

            Page<WalletTransferTransaction> matchedTransferTransactionPage = walletTransferTransactionRepository.
                    findAllByTransferRequestMapNotNullAndManagedUser_Id(id,new PageRequest(0,10, Sort.Direction.DESC, "id"));

//            Long ongoingTransactionsCount =
//                    walletTransferTransactionRepository.countAllByTransactionStatusInAndManagedUser_Id(GeneralUtil.getUnmatchedStatuses(),id);

           // Long completedTransactionsCount =
                    walletTransferTransactionRepository.countAllByTransactionStatusInAndManagedUser_Id(Collections.singletonList(TransactionStatus.COMPLETED),id);


            DashboardResponseModel dashboardResponseModel = new DashboardResponseModel();
            dashboardResponseModel.setNoOfOngoing(ongoingTransferTransactionPage.getTotalElements());
            dashboardResponseModel.setNoOfCompleted(completedTransferTransactionPage.getTotalElements());
            dashboardResponseModel.setNoOfMathed(matchedTransferTransactionPage.getTotalElements());
            dashboardResponseModel.setOnGoingTransactions(ongoingTransferTransactionPage);
            dashboardResponseModel.setCompletedTransactions(completedTransferTransactionPage);
            dashboardResponseModel.setMatchedTransactions(matchedTransferTransactionPage);

            return dashboardResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching transactions due to ", e);
            return DashboardResponseModel.fromNaration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching dashboard details");
        }
    }

    public DashboardResponseModel fetchOngoingTransactionDetailsByPage(WalletRequest walletRequest) {

        try {

            int page = walletRequest.getPageNo();
            int size = walletRequest.getPageSize();

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long id = 0L;

            if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
                id = userDetailsTokenEnvelope.getManagedUser().getId();
            }

            Page<WalletTransferTransaction> ongoingWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByTransactionStatusInAndManagedUser_Id(GeneralUtil.getUnmatchedStatuses(),id,new PageRequest(page,size, Sort.Direction.DESC,"id"));

            DashboardResponseModel dashboardResponseModel = new DashboardResponseModel();
            dashboardResponseModel.setOnGoingTransactions(ongoingWalletTransferTransactions);

            return dashboardResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching transactions due to ", e);
            return DashboardResponseModel.fromNaration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching ongoimng transactions details");
        }
    }

    public DashboardResponseModel fetchCompletedTransactionDetailsByPage(WalletRequest walletRequest) {

        try {

            int page = walletRequest.getPageNo();
            int size = walletRequest.getPageSize();

            UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long id = 0L;

            if (userDetailsTokenEnvelope != null && userDetailsTokenEnvelope.getManagedUser() != null) {
                id = userDetailsTokenEnvelope.getManagedUser().getId();
            }


            Page<WalletTransferTransaction> completedWalletTransferTransactions =
                    walletTransferTransactionRepository.findAllByTransactionStatusInAndManagedUser_Id(Collections.singletonList(TransactionStatus.COMPLETED),id,new PageRequest(page,size, Sort.Direction.DESC,"id"));

            DashboardResponseModel dashboardResponseModel = new DashboardResponseModel();
            dashboardResponseModel.setCompletedTransactions(completedWalletTransferTransactions);

            return dashboardResponseModel;
        } catch (Exception e) {
            logger.error("Error occurred while fetching transactions due to ", e);
            return DashboardResponseModel.fromNaration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while fetching completed transactions details");
        }
    }



}
