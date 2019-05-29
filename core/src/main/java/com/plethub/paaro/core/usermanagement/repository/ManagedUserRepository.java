package com.plethub.paaro.core.usermanagement.repository;


import com.plethub.paaro.core.models.ManagedUser;
import com.plethub.paaro.core.usermanagement.enums.UserCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ManagedUserRepository extends JpaRepository<ManagedUser,Long> {

    ManagedUser findOneByEmailAndPassword(String email, String password);

    ManagedUser findOneByEmail(String email);


    ManagedUser findOneByDisplayName(String displayName);

    Page<ManagedUser> findAllByEmailIsNotNull(Pageable pageable);

    Page<ManagedUser> findAllByFirstNameIsLikeOrLastNameIsLike(String firstName, String lastName, Pageable pageable);

    @Query("select m from ManagedUser m where m.email = :email OR m.displayName = :displayName")
    ManagedUser findManagedUserByEmailOrDisplayName(@Param("email") String email , @Param("displayName") String displayName);

    @Query("select m from ManagedUser m where m.email = :email OR m.displayName = :email")
    ManagedUser findManagedUserByEmailOrDisplayName(@Param("email") String email );

    Page<ManagedUser> findAllByUserCategory(UserCategory userCategory, Pageable pageable);

    @Query("select count(1) from ManagedUser m where m.dateCreated between :startDate and :endDate")
    Long getDashboardDetailsWithdateRange(@Param("startDate")Date startDate, @Param("endDate")Date endDate);

    ManagedUser findTopByEmailAndActivationCode(String email, String activationCode);

}
