package com.raizesdonordeste.backend.api;

import com.raizesdonordeste.backend.application.EstoqueService;
import com.raizesdonordeste.backend.domain.Estoque;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping("/unidades/{unidadeId}/produtos/{produtoId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE') or hasRole('ATENDENTE')")
    public ResponseEntity<?> consultar(@PathVariable Long unidadeId, @PathVariable Long produtoId) {
        try {
            return ResponseEntity.ok(estoqueService.consultar(unidadeId, produtoId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                "error", e.getMessage(),
                "message", "Estoque não encontrado."
            ));
        }
    }

    @PostMapping("/unidades/{unidadeId}/produtos/{produtoId}/entrada")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<?> entrada(@PathVariable Long unidadeId,
                                     @PathVariable Long produtoId,
                                     @RequestBody Map<String, Integer> body) {
        try {
            Estoque estoque = estoqueService.entrada(unidadeId, produtoId, body.get("quantidade"));
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                "error", e.getMessage(),
                "message", "Não foi possível registrar entrada no estoque."
            ));
        }
    }

    @PostMapping("/unidades/{unidadeId}/produtos/{produtoId}/saida")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<?> saida(@PathVariable Long unidadeId,
                                   @PathVariable Long produtoId,
                                   @RequestBody Map<String, Integer> body) {
        try {
            Estoque estoque = estoqueService.saida(unidadeId, produtoId, body.get("quantidade"));
            return ResponseEntity.ok(estoque);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("ESTOQUE_INSUFICIENTE")) {
                return ResponseEntity.status(409).body(Map.of(
                    "error", e.getMessage(),
                    "message", "Quantidade insuficiente em estoque."
                ));
            }
            return ResponseEntity.status(404).body(Map.of(
                "error", e.getMessage(),
                "message", "Não foi possível registrar saída no estoque."
            ));
        }
    }
}
