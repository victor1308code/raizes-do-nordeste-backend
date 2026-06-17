package com.raizesdonordeste.backend.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {

        String error = ex.getMessage() != null ? ex.getMessage() : "ERRO_INTERNO";
        int status = 400;

        if (error.contains("NAO_ENCONTRADO") || error.contains("NAO_ENCONTRADA")) status = 404;
        else if (error.equals("ESTOQUE_INSUFICIENTE")) status = 409;
        else if (error.equals("EMAIL_JA_CADASTRADO")) status = 409;
        else if (error.equals("CREDENCIAIS_INVALIDAS")) status = 401;
        else if (error.equals("USUARIO_INATIVO")) status = 403;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error);
        body.put("message", mensagemLegivel(error));
        body.put("details", List.of());
        body.put("timestamp", LocalDateTime.now());
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(status).body(body);
    }

    private String mensagemLegivel(String error) {
        return switch (error) {
            case "PEDIDO_NAO_ENCONTRADO" -> "Pedido não encontrado.";
            case "USUARIO_NAO_ENCONTRADO" -> "Usuário não encontrado.";
            case "PRODUTO_NAO_ENCONTRADO" -> "Produto não encontrado.";
            case "UNIDADE_NAO_ENCONTRADA" -> "Unidade não encontrada.";
            case "ESTOQUE_NAO_ENCONTRADO" -> "Estoque não encontrado para este produto nesta unidade.";
            case "ESTOQUE_INSUFICIENTE" -> "Quantidade insuficiente em estoque para um ou mais itens.";
            case "EMAIL_JA_CADASTRADO" -> "Este e-mail já está cadastrado.";
            case "CREDENCIAIS_INVALIDAS" -> "E-mail ou senha inválidos.";
            case "USUARIO_INATIVO" -> "Usuário inativo.";
            default -> "Ocorreu um erro inesperado.";
        };
    }
}
