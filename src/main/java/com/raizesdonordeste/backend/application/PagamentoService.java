package com.raizesdonordeste.backend.application;

import com.raizesdonordeste.backend.domain.Pagamento;
import com.raizesdonordeste.backend.domain.Pedido;
import com.raizesdonordeste.backend.infrastructure.PagamentoRepository;
import com.raizesdonordeste.backend.infrastructure.PedidoRepository;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository,
                            PedidoRepository pedidoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public Pagamento processar(Long pedidoId, String formaPagamento, boolean simularFalha) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("PEDIDO_NAO_ENCONTRADO"));

        Pagamento pagamento = new Pagamento();
        pagamento.setPedido(pedido);
        pagamento.setValor(pedido.getTotal());
        pagamento.setFormaPagamento(Pagamento.FormaPagamento.valueOf(formaPagamento));

        if (simularFalha) {
            pagamento.setStatusPagamento(Pagamento.StatusPagamento.RECUSADO);
            pagamento.setMensagemGateway("Pagamento recusado pelo gateway (simulação).");
        } else {
            pagamento.setStatusPagamento(Pagamento.StatusPagamento.APROVADO);
            pagamento.setMensagemGateway("Pagamento aprovado pelo gateway (simulação).");
            pedido.setStatus(Pedido.Status.PAGO);
            pedidoRepository.save(pedido);
        }

        return pagamentoRepository.save(pagamento);
    }

    public java.util.List<Pagamento> listarPorPedido(Long pedidoId) {
        return pagamentoRepository.findByPedidoId(pedidoId);
    }
}