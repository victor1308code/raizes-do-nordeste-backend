package com.raizesdonordeste.backend.api;

import com.raizesdonordeste.backend.application.FidelidadeService;
import com.raizesdonordeste.backend.domain.Fidelidade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/fidelidade")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    public FidelidadeController(FidelidadeService fidelidadeService) {
        this.fidelidadeService = fidelidadeService;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> consultar(@PathVariable Long usuarioId) {
        try {
            Fidelidade fidelidade = fidelidadeService.consultar(usuarioId);
            return ResponseEntity.ok(Map.of(
                "usuarioId", usuarioId,
                "pontos", fidelidade.getPontos()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{usuarioId}/resgatar")
    public ResponseEntity<?> resgatar(@PathVariable Long usuarioId) {
        try {
            Fidelidade fidelidade = fidelidadeService.resgatar(usuarioId);
            return ResponseEntity.ok(Map.of(
                "usuarioId", usuarioId,
                "pontosRestantes", fidelidade.getPontos(),
                "message", "Resgate realizado com sucesso! 100 pontos descontados."
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(422).body(Map.of("error", e.getMessage()));
        }
    }
}
