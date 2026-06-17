package com.raizesdonordeste.backend.application;

import com.raizesdonordeste.backend.domain.Estoque;
import com.raizesdonordeste.backend.domain.Produto;
import com.raizesdonordeste.backend.domain.Unidade;
import com.raizesdonordeste.backend.infrastructure.EstoqueRepository;
import com.raizesdonordeste.backend.infrastructure.ProdutoRepository;
import com.raizesdonordeste.backend.infrastructure.UnidadeRepository;
import org.springframework.stereotype.Service;

@Service
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;

    public EstoqueService(EstoqueRepository estoqueRepository,
                          UnidadeRepository unidadeRepository,
                          ProdutoRepository produtoRepository) {
        this.estoqueRepository = estoqueRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
    }

    public Estoque consultar(Long unidadeId, Long produtoId) {
        return estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                .orElseThrow(() -> new RuntimeException("ESTOQUE_NAO_ENCONTRADO"));
    }

    public Estoque entrada(Long unidadeId, Long produtoId, Integer quantidade) {
        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new RuntimeException("UNIDADE_NAO_ENCONTRADA"));

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("PRODUTO_NAO_ENCONTRADO"));

        Estoque estoque = estoqueRepository
                .findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                .orElseGet(() -> {
                    Estoque novo = new Estoque();
                    novo.setUnidade(unidade);
                    novo.setProduto(produto);
                    return novo;
                });

        estoque.setQuantidade(estoque.getQuantidade() + quantidade);
        return estoqueRepository.save(estoque);
    }

    public Estoque saida(Long unidadeId, Long produtoId, Integer quantidade) {
        Estoque estoque = consultar(unidadeId, produtoId);

        if (estoque.getQuantidade() < quantidade) {
            throw new RuntimeException("ESTOQUE_INSUFICIENTE");
        }

        estoque.setQuantidade(estoque.getQuantidade() - quantidade);
        return estoqueRepository.save(estoque);
    }
}