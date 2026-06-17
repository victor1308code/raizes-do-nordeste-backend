package com.raizesdonordeste.backend.api;

import com.raizesdonordeste.backend.application.PedidoService;
import com.raizesdonordeste.backend.domain.Pedido;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or hasRole('ATENDENTE') or hasRole('COZINHA')")
    public ResponseEntity<?> listar(@RequestParam(required = false) String canalPedido,
                                    @RequestParam(required = false) Long unidadeId,
                                    @RequestParam(required = false) String status) {
        try {
            if (canalPedido != null) {
                return ResponseEntity.ok(pedidoService.listarPorCanal(Pedido.CanalPedido.valueOf(canalPedido)));
            }
            if (unidadeId != null && status != null) {
                return ResponseEntity.ok(pedidoService.listarPorUnidadeEStatus(unidadeId, Pedido.Status.valueOf(status)));
            }
            return ResponseEntity.badRequest().body(Map.of(
                "error", "PARAMETRO_OBRIGATORIO",
                "message", "Informe canalPedido ou unidadeId+status para filtrar."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(Map.of(
                "error", "VALOR_INVALIDO",
                "message", "Canal ou status inválido."
            ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pedidoService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                "error", e.getMessage(),
                "message", "Pedido não encontrado."
            ));
        }
    }

    @GetMapping("/meus")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> meusPedidos(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(pedidoService.listarPorUsuario(usuarioId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> body) {
        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            Long unidadeId = Long.valueOf(body.get("unidadeId").toString());
            Pedido.CanalPedido canal = Pedido.CanalPedido.valueOf(body.get("canalPedido").toString());
            List<Map<String, Object>> itens = (List<Map<String, Object>>) body.get("itens");

            Pedido pedido = pedidoService.criar(usuarioId, unidadeId, canal, itens);
            return ResponseEntity.status(201).body(pedido);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ESTOQUE_INSUFICIENTE")) {
                return ResponseEntity.status(409).body(Map.of(
                    "error", e.getMessage(),
                    "message", "Quantidade insuficiente em estoque para um ou mais itens."
                ));
            }
            return ResponseEntity.status(404).body(Map.of(
                "error", e.getMessage(),
                "message", "Não foi possível criar o pedido."
            ));
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or hasRole('COZINHA') or hasRole('ATENDENTE')")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            Pedido.Status novoStatus = Pedido.Status.valueOf(body.get("status"));
            Pedido pedido = pedidoService.atualizarStatus(id, novoStatus);
            return ResponseEntity.ok(pedido);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(Map.of(
                "error", "STATUS_INVALIDO",
                "message", "Status inválido. Use: AGUARDANDO_PAGAMENTO, PAGO, EM_PREPARO, PRONTO, ENTREGUE, CANCELADO"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                "error", e.getMessage(),
                "message", "Pedido não encontrado."
            ));
        }
    }
}
