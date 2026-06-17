package com.raizesdonordeste.backend.application;

import com.raizesdonordeste.backend.domain.Unidade;
import com.raizesdonordeste.backend.infrastructure.UnidadeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    public UnidadeService(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    public List<Unidade> listarAtivas() {
        return unidadeRepository.findByAtivaTrue();
    }

    public Unidade buscarPorId(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("UNIDADE_NAO_ENCONTRADA"));
    }

    public Unidade cadastrar(String nome, String endereco, String telefone) {
        Unidade unidade = new Unidade();
        unidade.setNome(nome);
        unidade.setEndereco(endereco);
        unidade.setTelefone(telefone);
        return unidadeRepository.save(unidade);
    }

    public void desativar(Long id) {
        Unidade unidade = buscarPorId(id);
        unidade.setAtiva(false);
        unidadeRepository.save(unidade);
    }
}