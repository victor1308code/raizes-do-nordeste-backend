package com.raizesdonordeste.backend.infrastructure;

import com.raizesdonordeste.backend.domain.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UnidadeRepository extends JpaRepository<Unidade, Long> {
    List<Unidade> findByAtivaTrue();
}
