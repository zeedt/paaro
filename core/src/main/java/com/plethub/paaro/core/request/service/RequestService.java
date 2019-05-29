package com.plethub.paaro.core.request.service;

import com.plethub.paaro.core.appservice.enums.Action;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.appservice.repository.CurrencyRepository;
import com.plethub.paaro.core.appservice.services.*;
import com.plethub.paaro.core.authority.services.AuthorityService;
import com.plethub.paaro.core.infrastructure.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.models.Request;
import com.plethub.paaro.core.appservice.enums.Status;
import com.plethub.paaro.core.appservice.repository.RequestRepository;
import com.plethub.paaro.core.models.ManagedUser;
//import com.plethub.paaro.core.security.UserDetailsTokenEnvelope;
import com.plethub.paaro.core.request.apimodel.RequestResponse;
import com.plethub.paaro.core.request.apimodel.RequestSearchModel;
import com.plethub.paaro.core.usermanagement.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private BankService bankService;

    @Autowired
    private CurrencyDetailsService currencyDetailsService;

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private PaaroBankAccountService paaroBankAccountService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private BankDepositService bankDepositService;

    private Logger logger = LoggerFactory.getLogger(RequestService.class.getName());

    @Transactional
    public String logRequest( String oldData, String newData, Action action, String initiatorComment ) {

        ManagedUser initiator = getCurrentlyLoggedInUser();

        if (initiator == null) {
            return "Initiator not found";
        }

        Request request = new Request();
        request.setAction(action);
        request.setInitiatedDate(new Date());
        request.setNewData(newData);
        request.setOldData(oldData);
        request.setStatus(Status.PENDING_VERIFICATION);
        request.setInitiatorId(initiator);
        request.setIniatorComment(initiatorComment);

        requestRepository.save(request);

        return null;

    }

    public String logRequest (Long entityId, Action action) {

        ManagedUser initiator = getCurrentlyLoggedInUser();

        if (initiator == null) {
            return "Initiator not found";
        }

        Request request = new Request();
        request.setAction(action);
        request.setInitiatedDate(new Date());
        request.setStatus(Status.PENDING_VERIFICATION);
        request.setInitiatorId(initiator);
        request.setEntityId(entityId);

        requestRepository.save(request);

        return null;
    }

    public String logRequest (Long entityId, Action action, String oldData, String newData) {

        ManagedUser initiator = getCurrentlyLoggedInUser();

        if (initiator == null) {
            return "Initiator not found";
        }

        Request request = new Request();
        request.setAction(action);
        request.setInitiatedDate(new Date());
        request.setStatus(Status.PENDING_VERIFICATION);
        request.setInitiatorId(initiator);
        request.setEntityId(entityId);
        request.setNewData(newData);
        request.setOldData(oldData);

        requestRepository.save(request);

        return null;
    }


    public ManagedUser getCurrentlyLoggedInUser() {

        UserDetailsTokenEnvelope userDetailsTokenEnvelope = (UserDetailsTokenEnvelope)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (userDetailsTokenEnvelope == null || userDetailsTokenEnvelope.getManagedUser() == null || userDetailsTokenEnvelope.getManagedUser().getId() == null) {
            return null;
        }

        return userDetailsTokenEnvelope.getManagedUser();

    }

    public boolean isRequestPending(Action action, Long id) {

        if (requestRepository.findTopByActionAndEntityId(action, id) == null) return false;

        return true;

    }

    public RequestResponse getPagedPendingRequestByAction(Action action, Integer pageNo, Integer pageSize) {

        try {

            pageNo = pageNo == null || pageNo < 0 ? 0 : pageNo;
            pageSize = pageSize == null || pageNo < 1 ? 15 : pageSize;

            if (action == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Action cannoyt be null");

            return null;

        } catch (Exception e) {
            logger.error("Error occurred when getting paged pending request by action");
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.SYSTEM_ERROR, "System error occurred while getting paged pending request by action");
        }
    }

    public RequestResponse getPagedRequestByOrParameters(RequestSearchModel searchModel) {

        if (searchModel == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Requet object cannot be null");

        Integer pageNo = searchModel.getPageNo() == null || searchModel.getPageNo() < 0 ? 0 : searchModel.getPageNo();
        Integer pageSize = searchModel.getPageSize() == null || searchModel.getPageSize() < 1 ? 15 : searchModel.getPageSize();


        Page requestPage = requestRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.or(composePredicateBasedOnParams(searchModel, criteriaBuilder, root).toArray(new Predicate[0]));

        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        RequestResponse requestResponse = new RequestResponse();
        requestResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        requestResponse.setRequestPage(requestPage);

        return requestResponse;

    }

    public RequestResponse getPagedRequestByAndParameters(RequestSearchModel searchModel) {

        if (searchModel == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Requet object cannot be null");

        Integer pageNo = searchModel.getPageNo() == null || searchModel.getPageNo() < 0 ? 0 : searchModel.getPageNo();
        Integer pageSize = searchModel.getPageSize() == null || searchModel.getPageSize() < 1 ? 15 : searchModel.getPageSize();


        Page requestPage = requestRepository.findAll((root, criteriaQuery, criteriaBuilder) -> {

            return criteriaBuilder.and(composePredicateBasedOnParams(searchModel, criteriaBuilder, root).toArray(new Predicate[0]));

        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        RequestResponse requestResponse = new RequestResponse();
        requestResponse.setApiResponseCode(ApiResponseCode.SUCCESSFUL);
        requestResponse.setRequestPage(requestPage);

        return requestResponse;

    }

    private List<Predicate> composePredicateBasedOnParams(RequestSearchModel searchModel, CriteriaBuilder criteriaBuilder, Root root) {

        List<Predicate> predicateList = new ArrayList<>();
        if (searchModel.getEntityId() != null)
            predicateList.add(criteriaBuilder.equal(root.get("entityId"),searchModel.getEntityId()));
        if (searchModel.getId() != null)
            predicateList.add(criteriaBuilder.equal(root.get("id"),searchModel.getId()));
        if (searchModel.getStatus() != null)
            predicateList.add(criteriaBuilder.equal(root.get("status"),searchModel.getStatus()));
        if (!StringUtils.isEmpty(searchModel.getInitiator()))
            predicateList.add(criteriaBuilder.like(root.get("initiatorId").get("email"),searchModel.getInitiator()));
        if (!StringUtils.isEmpty(searchModel.getVerifier()))
            predicateList.add(criteriaBuilder.like(root.get("verifierId").get("email"),searchModel.getVerifier()));
        if (searchModel.getFromDate() != null && searchModel.getToDate() != null)
            predicateList.add(criteriaBuilder.between((root.get("initiatedDate")), searchModel.getFromDate(), searchModel.getToDate()));


        return predicateList;

    }

    @Transactional
    public RequestResponse verifyRequestById(Long id, HttpServletRequest servletRequest) throws IOException {

        Request request = requestRepository.findOne(id);

        if (request == null) return RequestResponse.fromCodeAndNarration(ApiResponseCode.NOT_FOUND, "Request not found");

        if (request.getStatus() == null || request.getStatus() != Status.PENDING_VERIFICATION)
            return RequestResponse.fromCodeAndNarration(ApiResponseCode.INVALID_REQUEST, "Request not awaiting verification");

        if (request.getAction() == Action.CREATE_USER)
            return userService.verifyUserCreationRequest(request, servletRequest);

        if (request.getAction() == Action.ADD_CURRENCY)
            return currencyService.verifyCurrencyAddition(request, servletRequest);

        if (request.getAction() == Action.ADD_CURRENCY_DETAILS)
            return currencyDetailsService.verifyCurrencyDetailsAddition(request, servletRequest);

        if (request.getAction() == Action.UPDATE_CURRENCY_DETAILS)
            return currencyDetailsService.verifyCurrencyDetailsUpdate(request, servletRequest);

        if (request.getAction() == Action.MAP_AUTHORITY_TO_USER )
            return authorityService.verifyAuthorityMappingToUser(request, servletRequest);

        if (request.getAction() == Action.MAP_AUTHORITIES_TO_USER )
            return authorityService.verifyAuthoritiesMappingToUser(request, servletRequest);

        if (request.getAction() == Action.DEACTIVATE_USER )
            return userService.verifyUserDeactivationRequest(request, servletRequest);

        if (request.getAction() == Action.ACTIVATE_USER )
            return userService.verifyUserActivationRequest(request, servletRequest);

        if (request.getAction() == Action.DISABLE_CURRENCY )
            return currencyService.verifyCurrencyDeactivationRequest(request, servletRequest);

        if (request.getAction() == Action.ENABLE_CURRENCY )
            return currencyService.verifyCurrencyActivationRequest(request, servletRequest);

        if (request.getAction() == Action.ADD_BANK )
            return bankService.verifyBankCreationRequest(request, servletRequest);

        if (request.getAction() == Action.UPDATE_BANK )
            return bankService.verifyBankUpdateRequest(request, servletRequest);

        if (request.getAction() == Action.CREATE_PAARO_BANK_ACCOUNT )
            return paaroBankAccountService.verifyBankCreationRequest(request, servletRequest);

        if (request.getAction() == Action.UPDATE_PAARO_BANK_ACCOUNT )
            return paaroBankAccountService.verifyBankUpdateRequest(request, servletRequest);

        if (request.getAction() == Action.SETTLE_CUSTOMER )
            return transferService.verifySettlement(request, servletRequest);

        if (request.getAction() == Action.VERIFY_DEPOSIT )
            return bankDepositService.verifyBankDeposit(request, servletRequest);

        return null;

    }

}
