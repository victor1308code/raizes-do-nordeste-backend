package com.raizesdonordeste.backend.api;

import com.raizesdonordeste.backend.application.UnidadeService;
import com.raizesdonordeste.backend.domain.Unidade;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {

    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @GetMapping
    public ResponseEntity<List<Unidade>> listar() {
        return ResponseEntity.ok(unidadeService.listarAtivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(unidadeService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", e.getMessage(),
                    "message", "Unidade não encontrada."
            ));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, String> body) {
        try {
            Unidade unidade = unidadeService.cadastrar(
                    body.get("nome"),
                    body.get("endereco"),
                    body.get("telefone")
            );
            return ResponseEntity.status(201).body(unidade);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "message", "Não foi possível cadastrar a unidade."
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            unidadeService.desativar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", e.getMessage(),
                    "message", "Unidade não encontrada."
            ));
        }
    }
}