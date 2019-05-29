package com.plethub.paaro.core.audit.service;

import com.plethub.paaro.core.appservice.apirequestmodel.AuditLogRequestModel;
import com.plethub.paaro.core.appservice.apiresponsemodel.AuditResponseModel;
import com.plethub.paaro.core.appservice.enums.ApiResponseCode;
import com.plethub.paaro.core.models.AuditLog;
import com.plethub.paaro.core.usermanagement.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogOperationsService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public AuditResponseModel fetchAuditLogWithOrParameters(AuditLogRequestModel logRequestModel) {

        if (logRequestModel == null) return AuditResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid Request");

        Integer pageNo = logRequestModel.getPageNo() == null || logRequestModel.getPageNo() < 0 ? 0 : logRequestModel.getPageNo();
        Integer pageSize = logRequestModel.getPageSize() == null || logRequestModel.getPageSize() < 1 ? 15 : logRequestModel.getPageSize();


        Page auditLogPage = auditLogRepository.findAll((Specification<AuditLog>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> searchPredicate = new ArrayList<>();

            if (logRequestModel.getModule() != null)
                searchPredicate.add(criteriaBuilder.equal((root.get("module")), logRequestModel.getModule()));
            if (logRequestModel.getEntityId() != null)
                searchPredicate.add(criteriaBuilder.equal((root.get("entityId")), logRequestModel.getEntityId()));
            if (logRequestModel.getId() != null)
                searchPredicate.add(criteriaBuilder.equal((root.get("id")), logRequestModel.getId()));
            if (!StringUtils.isEmpty(logRequestModel.getInitiator()))
                searchPredicate.add(criteriaBuilder.like((root.get("performedBy")), "%"+logRequestModel.getInitiator()+"%"));
            if (!StringUtils.isEmpty(logRequestModel.getIpAddress()))
                searchPredicate.add(criteriaBuilder.like((root.get("ipAddress")), "%"+logRequestModel.getIpAddress()+"%"));
            if (logRequestModel.getFromDate() != null && logRequestModel.getToDate() != null)
                searchPredicate.add(criteriaBuilder.between((root.get("datePerformed")), logRequestModel.getFromDate(), logRequestModel.getToDate()));

            return criteriaBuilder.or(searchPredicate.toArray(new Predicate[0]));
        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        AuditResponseModel auditResponseModel = new AuditResponseModel();

        auditResponseModel.setAuditLogPage(auditLogPage);
        auditResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);

        return auditResponseModel;

    }
    public AuditResponseModel fetchAuditLogWithAndParameters(AuditLogRequestModel logRequestModel) {

        if (logRequestModel == null) return AuditResponseModel.fromNarration(ApiResponseCode.INVALID_REQUEST, "Invalid Request");


        Integer pageNo = logRequestModel.getPageNo() == null || logRequestModel.getPageNo() < 0 ? 0 : logRequestModel.getPageNo();
        Integer pageSize = logRequestModel.getPageSize() == null || logRequestModel.getPageSize() < 1 ? 15 : logRequestModel.getPageSize();


        Page auditLogPage = auditLogRepository.findAll((Specification<AuditLog>) (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> searchPredicate = new ArrayList<>();

            if (logRequestModel.getModule() != null)
                searchPredicate.add(criteriaBuilder.equal((root.get("module")), logRequestModel.getModule()));
            if (logRequestModel.getEntityId() != null)
                searchPredicate.add(criteriaBuilder.equal((root.get("entityId")), logRequestModel.getEntityId()));
            if (logRequestModel.getId() != null)
                searchPredicate.add(criteriaBuilder.equal((root.get("id")), logRequestModel.getId()));
            if (!StringUtils.isEmpty(logRequestModel.getInitiator()))
                searchPredicate.add(criteriaBuilder.like((root.get("performedBy")), "%"+logRequestModel.getInitiator()+"%"));
            if (!StringUtils.isEmpty(logRequestModel.getIpAddress()))
                searchPredicate.add(criteriaBuilder.like((root.get("ipAddress")), "%"+logRequestModel.getIpAddress()+"%"));
            if (logRequestModel.getFromDate() != null && logRequestModel.getToDate() != null)
                searchPredicate.add(criteriaBuilder.between((root.get("datePerformed")), logRequestModel.getFromDate(), logRequestModel.getToDate()));

            return criteriaBuilder.and(searchPredicate.toArray(new Predicate[0]));
        }, new PageRequest(pageNo, pageSize, Sort.Direction.DESC, "id"));

        AuditResponseModel auditResponseModel = new AuditResponseModel();

        auditResponseModel.setAuditLogPage(auditLogPage);
        auditResponseModel.setApiResponseCode(ApiResponseCode.SUCCESSFUL);

        return auditResponseModel;

    }

}
