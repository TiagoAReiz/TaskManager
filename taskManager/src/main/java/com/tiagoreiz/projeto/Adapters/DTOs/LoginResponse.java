package com.tiagoreiz.projeto.Adapters.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de login de usuário.
 * Contém o token JWT e informações básicas do usuário autenticado.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * Token JWT para autenticação
     */
    private String token;
    
    /**
     * Tipo do token (sempre "Bearer")
     */
    private String tokenType = "Bearer";
    
    /**
     * ID do usuário autenticado
     */
    private Long userId;
    
    /**
     * Nome do usuário autenticado
     */
    private String name;
    
    /**
     * Email do usuário autenticado
     */
    private String email;
    
    /**
     * Data de criação da conta do usuário
     */
    private LocalDateTime userCreatedAt;
    
    /**
     * Construtor principal (sem o tokenType que tem valor padrão)
     * 
     * @param token Token JWT
     * @param userId ID do usuário
     * @param name Nome do usuário
     * @param email Email do usuário
     * @param userCreatedAt Data de criação da conta
     */
    public LoginResponse(String token, Long userId, String name, String email, LocalDateTime userCreatedAt) {
        this.token = token;
        this.tokenType = "Bearer";
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.userCreatedAt = userCreatedAt;
    }
}