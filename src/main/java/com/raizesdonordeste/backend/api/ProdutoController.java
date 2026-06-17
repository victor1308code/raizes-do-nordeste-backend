package com.raizesdonordeste.backend.api;

import com.raizesdonordeste.backend.application.ProdutoService;
import com.raizesdonordeste.backend.domain.Produto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listar(@RequestParam(required = false) String categoria) {
        if (categoria != null) {
            return ResponseEntity.ok(produtoService.listarPorCategoria(categoria));
        }
        return ResponseEntity.ok(produtoService.listarDisponiveis());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(produtoService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                "error", e.getMessage(),
                "message", "Produto não encontrado."
            ));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<?> cadastrar(@RequestBody Map<String, Object> body) {
        try {
            Produto produto = produtoService.cadastrar(
                (String) body.get("nome"),
                (String) body.get("descricao"),
                new BigDecimal(body.get("preco").toString()),
                (String) body.get("categoria")
            );
            return ResponseEntity.status(201).body(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "message", "Não foi possível cadastrar o produto."
            ));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('GERENTE')")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Produto produto = produtoService.atualizar(
                id,
                (String) body.get("nome"),
                (String) body.get("descricao"),
                new BigDecimal(body.get("preco").toString()),
                (String) body.get("categoria")
            );
            return ResponseEntity.ok(produto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                "error", e.getMessage(),
                "message", "Produto não encontrado."
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        try {
            produtoService.desativar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                "error", e.getMessage(),
                "message", "Produto não encontrado."
            ));
        }
    }
}
