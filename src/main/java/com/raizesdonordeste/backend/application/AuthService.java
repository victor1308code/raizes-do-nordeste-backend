package com.raizesdonordeste.backend.application;

import com.raizesdonordeste.backend.domain.Fidelidade;
import com.raizesdonordeste.backend.domain.Usuario;
import com.raizesdonordeste.backend.infrastructure.FidelidadeRepository;
import com.raizesdonordeste.backend.infrastructure.UsuarioRepository;
import com.raizesdonordeste.backend.infrastructure.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final FidelidadeRepository fidelidadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository,
                       FidelidadeRepository fidelidadeRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.fidelidadeRepository = fidelidadeRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Usuario cadastrar(String nome, String email, String senha, boolean consentimentoLgpd, Usuario.Role role) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("EMAIL_JA_CADASTRADO");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setRole(role != null ? role : Usuario.Role.CLIENTE);
        usuario.setConsentimentoLgpd(consentimentoLgpd);

        usuarioRepository.save(usuario);

        Fidelidade fidelidade = new Fidelidade();
        fidelidade.setUsuario(usuario);
        fidelidadeRepository.save(fidelidade);

        return usuario;
    }

    public String login(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("CREDENCIAIS_INVALIDAS"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new RuntimeException("CREDENCIAIS_INVALIDAS");
        }

        if (!usuario.isAtivo()) {
            throw new RuntimeException("USUARIO_INATIVO");
        }

        return jwtUtil.gerarToken(usuario.getEmail(), usuario.getRole().name());
    }
}