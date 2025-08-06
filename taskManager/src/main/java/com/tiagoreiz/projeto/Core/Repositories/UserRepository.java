package com.tiagoreiz.projeto.Core.Repositories;

import com.tiagoreiz.projeto.Core.Entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório de usuários seguindo os princípios da Clean Architecture.
 * Define os contratos para persistência de usuários sem depender de implementações específicas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
public interface UserRepository {
    
    /**
     * Salva um usuário no repositório
     * 
     * @param user O usuário a ser salvo
     * @return O usuário salvo com ID gerado
     */
    User save(User user);
    
    /**
     * Busca um usuário pelo ID
     * 
     * @param id O ID do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findById(Long id);
    
    /**
     * Busca um usuário pelo email
     * 
     * @param email O email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Lista todos os usuários
     * 
     * @return Lista de todos os usuários
     */
    List<User> findAll();
    
    /**
     * Verifica se existe um usuário com o email fornecido
     * 
     * @param email O email a ser verificado
     * @return true se existe um usuário com esse email
     */
    boolean existsByEmail(String email);
    
    /**
     * Remove um usuário pelo ID
     * 
     * @param id O ID do usuário a ser removido
     */
    void deleteById(Long id);
    
    /**
     * Atualiza um usuário existente
     * 
     * @param user O usuário com dados atualizados
     * @return O usuário atualizado
     */
    User update(User user);
}