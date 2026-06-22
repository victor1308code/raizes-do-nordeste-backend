package com.raizesdonordeste.backend.application;

import com.raizesdonordeste.backend.domain.Fidelidade;
import com.raizesdonordeste.backend.infrastructure.FidelidadeRepository;
import org.springframework.stereotype.Service;

@Service
public class FidelidadeService {

    private static final int PONTOS_POR_RESGATE = 100;

    private final FidelidadeRepository fidelidadeRepository;

    public FidelidadeService(FidelidadeRepository fidelidadeRepository) {
        this.fidelidadeRepository = fidelidadeRepository;
    }

    public Fidelidade consultar(Long usuarioId) {
        return fidelidadeRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Fidelidade não encontrada para o usuário " + usuarioId));
    }

    public Fidelidade resgatar(Long usuarioId) {
        Fidelidade fidelidade = consultar(usuarioId);

        if (fidelidade.getPontos() < PONTOS_POR_RESGATE) {
            throw new RuntimeException("Pontos insuficientes. Mínimo para resgate: " + PONTOS_POR_RESGATE);
        }

        fidelidade.setPontos(fidelidade.getPontos() - PONTOS_POR_RESGATE);
        return fidelidadeRepository.save(fidelidade);
    }
}
