package com.raizesdonordeste.backend.api;

import com.raizesdonordeste.backend.application.PagamentoService;
import com.raizesdonordeste.backend.domain.Pagamento;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> processar(@RequestBody Map<String, Object> body) {
        try {
            Long pedidoId = Long.valueOf(body.get("pedidoId").toString());
            String formaPagamento = body.get("formaPagamento").toString();
            boolean simularFalha = body.containsKey("simularFalha")
                    && Boolean.parseBoolean(body.get("simularFalha").toString());

            Pagamento pagamento = pagamentoService.processar(pedidoId, formaPagamento, simularFalha);
            return ResponseEntity.status(201).body(pagamento);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(Map.of(
                    "error", "FORMA_PAGAMENTO_INVALIDA",
                    "message", "Formas válidas: PIX, CARTAO_CREDITO, CARTAO_DEBITO, DINHEIRO, MOCK"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", e.getMessage(),
                    "message", "Pedido não encontrado."
            ));
        }
    }

    @GetMapping("/pedido/{pedidoId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listarPorPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pagamentoService.listarPorPedido(pedidoId));
    }
}