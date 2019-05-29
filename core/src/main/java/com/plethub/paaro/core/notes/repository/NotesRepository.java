package com.plethub.paaro.core.notes.repository;

import com.plethub.paaro.core.models.ManagedUserAuthority;
import com.plethub.paaro.core.models.Notes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotesRepository extends JpaRepository<Notes,Long> {

    List<Notes> findAllByCustomer_Id(Long id);

    List<Notes> findAllByCustomer_IdIn(List<Long> id);

    Page<Notes> findAllByCustomer_Id(Long id, Pageable pageable);

}
