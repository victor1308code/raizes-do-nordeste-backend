package com.raizesdonordeste.backend.infrastructure;

import com.raizesdonordeste.backend.domain.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    Optional<Estoque> findByUnidadeIdAndProdutoId(Long unidadeId, Long produtoId);
}
