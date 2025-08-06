package com.tiagoreiz.projeto.Infra.Persistence.SpringData;

import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import com.tiagoreiz.projeto.Infra.Persistence.Entities.TaskPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório JPA para operações de persistência da entidade TaskPersistence.
 * Estende JpaRepository para operações CRUD básicas e define consultas específicas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Repository
public interface TaskJpaRepository extends JpaRepository<TaskPersistence, Long> {
    
    /**
     * Busca todas as tarefas de um usuário
     * 
     * @param userId ID do usuário
     * @return Lista de tarefas do usuário
     */
    List<TaskPersistence> findByUserId(Long userId);
    
    /**
     * Busca tarefas de um usuário filtradas por status
     * 
     * @param userId ID do usuário
     * @param status Status das tarefas
     * @return Lista de tarefas com o status especificado
     */
    List<TaskPersistence> findByUserIdAndStatus(Long userId, TaskStatus status);
    
    /**
     * Busca tarefas de um usuário filtradas por prioridade
     * 
     * @param userId ID do usuário
     * @param priority Prioridade das tarefas
     * @return Lista de tarefas com a prioridade especificada
     */
    List<TaskPersistence> findByUserIdAndPriority(Long userId, TaskPriority priority);
    
    /**
     * Busca tarefas de um usuário filtradas por status e prioridade
     * 
     * @param userId ID do usuário
     * @param status Status das tarefas
     * @param priority Prioridade das tarefas
     * @return Lista de tarefas com o status e prioridade especificados
     */
    List<TaskPersistence> findByUserIdAndStatusAndPriority(Long userId, TaskStatus status, TaskPriority priority);
    
    /**
     * Busca tarefas de um usuário que vencem antes de uma data específica
     * 
     * @param userId ID do usuário
     * @param dueDate Data limite
     * @return Lista de tarefas que vencem antes da data especificada
     */
    List<TaskPersistence> findByUserIdAndDueDateBefore(Long userId, LocalDateTime dueDate);
    
    /**
     * Busca tarefas de um usuário que estão atrasadas (pendentes e vencidas)
     * 
     * @param userId ID do usuário
     * @param currentDateTime Data e hora atual
     * @return Lista de tarefas atrasadas
     */
    @Query("SELECT t FROM TaskPersistence t WHERE t.user.id = :userId AND t.status = 'PENDING' AND t.dueDate < :currentDateTime")
    List<TaskPersistence> findOverdueTasksByUserId(@Param("userId") Long userId, @Param("currentDateTime") LocalDateTime currentDateTime);
    
    /**
     * Conta o número de tarefas de um usuário por status
     * 
     * @param userId ID do usuário
     * @param status Status das tarefas
     * @return Número de tarefas com o status especificado
     */
    long countByUserIdAndStatus(Long userId, TaskStatus status);
    
    /**
     * Remove todas as tarefas de um usuário
     * 
     * @param userId ID do usuário
     */
    void deleteByUserId(Long userId);
    
    /**
     * Busca tarefas de um usuário ordenadas por data de vencimento
     * 
     * @param userId ID do usuário
     * @return Lista de tarefas ordenadas por data de vencimento
     */
    @Query("SELECT t FROM TaskPersistence t WHERE t.user.id = :userId ORDER BY t.dueDate ASC")
    List<TaskPersistence> findByUserIdOrderByDueDateAsc(@Param("userId") Long userId);
    
    /**
     * Busca tarefas de um usuário ordenadas por prioridade (HIGH, MEDIUM, LOW)
     * 
     * @param userId ID do usuário
     * @return Lista de tarefas ordenadas por prioridade
     */
    @Query("SELECT t FROM TaskPersistence t WHERE t.user.id = :userId ORDER BY CASE t.priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 END")
    List<TaskPersistence> findByUserIdOrderByPriority(@Param("userId") Long userId);
    
    /**
     * Busca tarefas de um usuário que contêm determinado texto no título ou descrição
     * 
     * @param userId ID do usuário
     * @param searchText Texto a ser buscado
     * @return Lista de tarefas que contêm o texto
     */
    @Query("SELECT t FROM TaskPersistence t WHERE t.user.id = :userId AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<TaskPersistence> findByUserIdAndTitleOrDescriptionContaining(@Param("userId") Long userId, @Param("searchText") String searchText);
}