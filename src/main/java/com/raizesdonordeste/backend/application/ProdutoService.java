package com.raizesdonordeste.backend.application;

import com.raizesdonordeste.backend.domain.Produto;
import com.raizesdonordeste.backend.infrastructure.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarDisponiveis() {
        return produtoRepository.findByDisponivelTrue();
    }

    public List<Produto> listarPorCategoria(String categoria) {
        return produtoRepository.findByCategoria(categoria);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PRODUTO_NAO_ENCONTRADO"));
    }

    public Produto cadastrar(String nome, String descricao, BigDecimal preco, String categoria) {
        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setCategoria(categoria);
        return produtoRepository.save(produto);
    }

    public Produto atualizar(Long id, String nome, String descricao, BigDecimal preco, String categoria) {
        Produto produto = buscarPorId(id);
        produto.setNome(nome);
        produto.setDescricao(descricao);
        produto.setPreco(preco);
        produto.setCategoria(categoria);
        return produtoRepository.save(produto);
    }

    public void desativar(Long id) {
        Produto produto = buscarPorId(id);
        produto.setDisponivel(false);
        produtoRepository.save(produto);
    }
}
