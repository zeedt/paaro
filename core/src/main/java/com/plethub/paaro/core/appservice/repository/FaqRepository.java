package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.Faq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

    Faq findTopByQuestion(String question);

    Page<Faq> findAllByIdNotNull(Pageable pageable);

}
