package com.tiagoreiz.projeto.Application.UseCases.Task;

import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import com.tiagoreiz.projeto.Core.Repositories.TaskRepository;
import com.tiagoreiz.projeto.Core.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Caso de uso para buscar tarefas de um usuário com filtros opcionais.
 * Responsável pela lógica de negócio da listagem e filtragem de tarefas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class GetUserTasksUseCase {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    /**
     * Busca todas as tarefas de um usuário
     * 
     * @param userId ID do usuário
     * @return Lista de tarefas do usuário
     * @throws IllegalArgumentException Se o usuário não existe
     */
    public List<Task> execute(Long userId) {
        validateUserId(userId);
        return taskRepository.findByUserId(userId);
    }
    
    /**
     * Busca tarefas de um usuário filtradas por status
     * 
     * @param userId ID do usuário
     * @param status Status das tarefas
     * @return Lista de tarefas filtradas por status
     * @throws IllegalArgumentException Se o usuário não existe
     */
    public List<Task> executeByStatus(Long userId, TaskStatus status) {
        validateUserId(userId);
        
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        return taskRepository.findByUserIdAndStatus(userId, status);
    }
    
    /**
     * Busca tarefas de um usuário filtradas por prioridade
     * 
     * @param userId ID do usuário
     * @param priority Prioridade das tarefas
     * @return Lista de tarefas filtradas por prioridade
     * @throws IllegalArgumentException Se o usuário não existe
     */
    public List<Task> executeByPriority(Long userId, TaskPriority priority) {
        validateUserId(userId);
        
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        
        return taskRepository.findByUserIdAndPriority(userId, priority);
    }
    
    /**
     * Busca tarefas de um usuário filtradas por status e prioridade
     * 
     * @param userId ID do usuário
     * @param status Status das tarefas
     * @param priority Prioridade das tarefas
     * @return Lista de tarefas filtradas por status e prioridade
     * @throws IllegalArgumentException Se o usuário não existe
     */
    public List<Task> executeByStatusAndPriority(Long userId, TaskStatus status, TaskPriority priority) {
        validateUserId(userId);
        
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        
        return taskRepository.findByUserIdAndStatusAndPriority(userId, status, priority);
    }
    
    /**
     * Busca tarefas atrasadas de um usuário
     * 
     * @param userId ID do usuário
     * @return Lista de tarefas atrasadas
     * @throws IllegalArgumentException Se o usuário não existe
     */
    public List<Task> executeOverdueTasks(Long userId) {
        validateUserId(userId);
        return taskRepository.findOverdueTasksByUserId(userId);
    }
    
    /**
     * Conta o número de tarefas de um usuário por status
     * 
     * @param userId ID do usuário
     * @param status Status das tarefas
     * @return Número de tarefas com o status especificado
     * @throws IllegalArgumentException Se o usuário não existe
     */
    public long countByStatus(Long userId, TaskStatus status) {
        validateUserId(userId);
        
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        return taskRepository.countByUserIdAndStatus(userId, status);
    }
    
    /**
     * Valida se o usuário existe
     * 
     * @param userId ID do usuário
     * @throws IllegalArgumentException Se o ID é inválido ou usuário não existe
     */
    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        
        if (!userRepository.findById(userId).isPresent()) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }
}