package com.plethub.paaro.core.usermanagement.repository;

import com.plethub.paaro.core.models.ManagedUserAuthority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagedUserAuthorityRepository extends JpaRepository<ManagedUserAuthority,Long> {

    List<ManagedUserAuthority> findAllByManagedUserId(Long id);

    List<ManagedUserAuthority> findAllByManagedUserIdIn(List<Long> id);

    List<ManagedUserAuthority> findAllByManagedUserIdAndAuthorityId(Long managedUserId, Long authorityId);

}
