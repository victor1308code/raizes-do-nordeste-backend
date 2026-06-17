package com.raizesdonordeste.backend.application;

import com.raizesdonordeste.backend.domain.*;
import com.raizesdonordeste.backend.infrastructure.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final FidelidadeRepository fidelidadeRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         UsuarioRepository usuarioRepository,
                         UnidadeRepository unidadeRepository,
                         ProdutoRepository produtoRepository,
                         EstoqueRepository estoqueRepository,
                         FidelidadeRepository fidelidadeRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.fidelidadeRepository = fidelidadeRepository;
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> listarPorCanal(Pedido.CanalPedido canal) {
        return pedidoRepository.findByCanalPedido(canal);
    }

    public List<Pedido> listarPorUnidadeEStatus(Long unidadeId, Pedido.Status status) {
        return pedidoRepository.findByUnidadeIdAndStatus(unidadeId, status);
    }

    public Pedido buscarPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PEDIDO_NAO_ENCONTRADO"));
    }

    public Pedido criar(Long usuarioId, Long unidadeId, Pedido.CanalPedido canal, List<Map<String, Object>> itensRequest) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("USUARIO_NAO_ENCONTRADO"));

        Unidade unidade = unidadeRepository.findById(unidadeId)
                .orElseThrow(() -> new RuntimeException("UNIDADE_NAO_ENCONTRADA"));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setUnidade(unidade);
        pedido.setCanalPedido(canal);

        BigDecimal total = BigDecimal.ZERO;

        for (Map<String, Object> itemRequest : itensRequest) {
            Long produtoId = Long.valueOf(itemRequest.get("produtoId").toString());
            Integer quantidade = Integer.valueOf(itemRequest.get("quantidade").toString());

            Produto produto = produtoRepository.findById(produtoId)
                    .orElseThrow(() -> new RuntimeException("PRODUTO_NAO_ENCONTRADO"));

            Estoque estoque = estoqueRepository.findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                    .orElseThrow(() -> new RuntimeException("ESTOQUE_NAO_ENCONTRADO"));

            if (estoque.getQuantidade() < quantidade) {
                throw new RuntimeException("ESTOQUE_INSUFICIENTE");
            }

            estoque.setQuantidade(estoque.getQuantidade() - quantidade);
            estoqueRepository.save(estoque);

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPreco());

            pedido.getItens().add(item);
            total = total.add(produto.getPreco().multiply(BigDecimal.valueOf(quantidade)));
        }

        pedido.setTotal(total);
        return pedidoRepository.save(pedido);
    }

    public Pedido atualizarStatus(Long pedidoId, Pedido.Status novoStatus) {
        Pedido pedido = buscarPorId(pedidoId);
        pedido.setStatus(novoStatus);

        if (novoStatus == Pedido.Status.ENTREGUE) {
            fidelidadeRepository.findByUsuarioId(pedido.getUsuario().getId())
                    .ifPresent(fidelidade -> {
                        int pontos = pedido.getTotal().intValue();
                        fidelidade.setPontos(fidelidade.getPontos() + pontos);
                        fidelidadeRepository.save(fidelidade);
                    });
        }

        return pedidoRepository.save(pedido);
    }
}