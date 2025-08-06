package com.tiagoreiz.projeto.Infra.Persistence.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entidade JPA que representa um usuário na camada de persistência.
 * Responsável pelo mapeamento objeto-relacional da entidade User do domínio.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPersistence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TaskPersistence> tasks;
    
    /**
     * Construtor para criação de um novo usuário (sem ID)
     * 
     * @param name Nome do usuário
     * @param email Email do usuário
     * @param password Senha criptografada
     */
    public UserPersistence(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}