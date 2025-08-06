package com.tiagoreiz.projeto.Infra.Persistence.SpringData;

import com.tiagoreiz.projeto.Infra.Persistence.Entities.UserPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para operações de persistência da entidade UserPersistence.
 * Estende JpaRepository para operações CRUD básicas e define consultas específicas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserPersistence, Long> {
    
    /**
     * Busca um usuário pelo email
     * 
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<UserPersistence> findByEmail(String email);
    
    /**
     * Verifica se existe um usuário com o email fornecido
     * 
     * @param email Email a ser verificado
     * @return true se existe um usuário com esse email
     */
    boolean existsByEmail(String email);
    
    /**
     * Busca um usuário pelo email ignorando maiúsculas e minúsculas
     * 
     * @param email Email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<UserPersistence> findByEmailIgnoreCase(String email);
    
    /**
     * Conta o número de usuários cadastrados
     * 
     * @return Número total de usuários
     */
    @Query("SELECT COUNT(u) FROM UserPersistence u")
    long countUsers();
    
    /**
     * Busca usuários cujo nome contém o texto fornecido (busca case-insensitive)
     * 
     * @param name Texto a ser buscado no nome
     * @return Lista de usuários encontrados
     */
    @Query("SELECT u FROM UserPersistence u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    java.util.List<UserPersistence> findByNameContainingIgnoreCase(@Param("name") String name);
}