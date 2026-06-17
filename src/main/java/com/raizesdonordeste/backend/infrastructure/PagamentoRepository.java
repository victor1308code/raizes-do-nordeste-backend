package com.raizesdonordeste.backend.infrastructure;

import com.raizesdonordeste.backend.domain.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByPedidoId(Long pedidoId);
}