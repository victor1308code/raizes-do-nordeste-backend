package com.raizesdonordeste.backend.infrastructure;

import com.raizesdonordeste.backend.domain.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FidelidadeRepository extends JpaRepository<Fidelidade, Long> {
    Optional<Fidelidade> findByUsuarioId(Long usuarioId);
}
