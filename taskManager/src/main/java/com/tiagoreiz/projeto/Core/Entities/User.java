package com.tiagoreiz.projeto.Core.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade de domínio que representa um usuário no sistema TaskMaster.
 * Esta é uma entidade pura da camada Core, sem dependências externas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * Identificador único do usuário
     */
    private Long id;
    
    /**
     * Nome completo do usuário
     */
    private String name;
    
    /**
     * Email único do usuário (usado para login)
     */
    private String email;
    
    /**
     * Senha criptografada do usuário
     */
    private String password;
    
    /**
     * Data e hora de criação do usuário
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização do usuário
     */
    private LocalDateTime updatedAt;
    
    /**
     * Lista de tarefas associadas ao usuário
     */
    private List<Task> tasks;
    
    /**
     * Construtor para criação de um novo usuário (sem ID)
     * 
     * @param name Nome do usuário
     * @param email Email do usuário
     * @param password Senha criptografada
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Atualiza o timestamp de última modificação
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}