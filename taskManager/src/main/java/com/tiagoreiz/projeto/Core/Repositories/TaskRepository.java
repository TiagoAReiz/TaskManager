package com.tiagoreiz.projeto.Core.Repositories;

import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório de tarefas seguindo os princípios da Clean Architecture.
 * Define os contratos para persistência de tarefas sem depender de implementações específicas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
public interface TaskRepository {
    
    /**
     * Salva uma tarefa no repositório
     * 
     * @param task A tarefa a ser salva
     * @return A tarefa salva com ID gerado
     */
    Task save(Task task);
    
    /**
     * Busca uma tarefa pelo ID
     * 
     * @param id O ID da tarefa
     * @return Optional contendo a tarefa se encontrada
     */
    Optional<Task> findById(Long id);
    
    /**
     * Lista todas as tarefas de um usuário
     * 
     * @param userId O ID do usuário
     * @return Lista de tarefas do usuário
     */
    List<Task> findByUserId(Long userId);
    
    /**
     * Lista todas as tarefas de um usuário filtradas por status
     * 
     * @param userId O ID do usuário
     * @param status O status das tarefas
     * @return Lista de tarefas do usuário com o status especificado
     */
    List<Task> findByUserIdAndStatus(Long userId, TaskStatus status);
    
    /**
     * Lista todas as tarefas de um usuário filtradas por prioridade
     * 
     * @param userId O ID do usuário
     * @param priority A prioridade das tarefas
     * @return Lista de tarefas do usuário com a prioridade especificada
     */
    List<Task> findByUserIdAndPriority(Long userId, TaskPriority priority);
    
    /**
     * Lista todas as tarefas de um usuário filtradas por status e prioridade
     * 
     * @param userId O ID do usuário
     * @param status O status das tarefas
     * @param priority A prioridade das tarefas
     * @return Lista de tarefas do usuário com o status e prioridade especificados
     */
    List<Task> findByUserIdAndStatusAndPriority(Long userId, TaskStatus status, TaskPriority priority);
    
    /**
     * Lista todas as tarefas de um usuário que vencem antes de uma data específica
     * 
     * @param userId O ID do usuário
     * @param dueDate A data limite
     * @return Lista de tarefas que vencem antes da data especificada
     */
    List<Task> findByUserIdAndDueDateBefore(Long userId, LocalDateTime dueDate);
    
    /**
     * Lista todas as tarefas de um usuário que estão atrasadas (pendentes e vencidas)
     * 
     * @param userId O ID do usuário
     * @return Lista de tarefas atrasadas do usuário
     */
    List<Task> findOverdueTasksByUserId(Long userId);
    
    /**
     * Conta o número de tarefas de um usuário por status
     * 
     * @param userId O ID do usuário
     * @param status O status das tarefas
     * @return Número de tarefas com o status especificado
     */
    long countByUserIdAndStatus(Long userId, TaskStatus status);
    
    /**
     * Remove uma tarefa pelo ID
     * 
     * @param id O ID da tarefa a ser removida
     */
    void deleteById(Long id);
    
    /**
     * Remove todas as tarefas de um usuário
     * 
     * @param userId O ID do usuário
     */
    void deleteByUserId(Long userId);
    
    /**
     * Atualiza uma tarefa existente
     * 
     * @param task A tarefa com dados atualizados
     * @return A tarefa atualizada
     */
    Task update(Task task);
    
    /**
     * Lista todas as tarefas
     * 
     * @return Lista de todas as tarefas
     */
    List<Task> findAll();
}