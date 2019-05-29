package com.plethub.paaro.core.usermanagement.repository;

import com.plethub.paaro.core.usermanagement.enums.Module;
import com.plethub.paaro.core.models.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog,Long>, JpaSpecificationExecutor {


    AuditLog findTopByModuleIsOrderByIdDesc(Module module);


    @Query("select a from AuditLog a where a.performedBy like :filter OR a.action like :filter OR a.module like :filter")
    Page<AuditLog> findLogWithFilter(@Param("filter") String filter, Pageable pageable);

    @Query("select a from AuditLog a where a.performedBy like :filter OR a.action like :filter OR a.module like :filter")
    List<AuditLog> findLogWithFilter(@Param("filter") String filter);

    @Query("select a from AuditLog a where a.datePerformed between :fromDate AND  :toDate AND (a.performedBy like :filter OR a.action like :filter OR a.module like :filter)")
    Page<AuditLog> findLogWithFilterPlusDateRange(@Param("filter") String filter, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate, Pageable pageable);

    @Query("select a from AuditLog a where a.datePerformed between :fromDate AND  :toDate AND (a.performedBy like :filter OR a.action like :filter OR a.module like :filter)")
    List<AuditLog> findLogWithFilterPlusDateRange(@Param("filter") String filter, @Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    Page<AuditLog> findAllByModule(Module module, Pageable pageable);

    Page<AuditLog> findAllByPerformedBy(String initiator, Pageable pageable);

    Page<AuditLog> findAllByDatePerformedBetween(Date fromDate, Date toDate, Pageable pageable);


}
