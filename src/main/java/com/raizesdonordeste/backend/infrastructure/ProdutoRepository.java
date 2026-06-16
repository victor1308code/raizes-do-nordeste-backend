package com.raizesdonordeste.backend.infrastructure;

import com.raizesdonordeste.backend.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByDisponivelTrue();
    List<Produto> findByCategoria(String categoria);
}
