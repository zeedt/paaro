package com.plethub.paaro.core.appservice.repository;

import com.plethub.paaro.core.models.Tokens;
import com.plethub.paaro.core.usermanagement.enums.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokensRepository extends JpaRepository<Tokens,Long> {

    Tokens findByEmailAndModuleAndTokenOrderByIdDesc(String email, Module module, String token);

}
