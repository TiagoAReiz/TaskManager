package com.tiagoreiz.projeto.Application.UseCases.Task;

import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para exclusão de tarefas do sistema.
 * Responsável pela lógica de negócio da remoção de tarefas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DeleteTaskUseCase {
    
    private final TaskRepository taskRepository;
    
    /**
     * Remove uma tarefa do sistema
     * 
     * @param taskId ID da tarefa a ser removida
     * @param userId ID do usuário (para verificação de propriedade)
     * @throws IllegalArgumentException Se a tarefa não é encontrada ou usuário não autorizado
     */
    public void execute(Long taskId, Long userId) {
        // Validações de entrada
        validateInput(taskId, userId);
        
        // Busca a tarefa existente
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Verifica se o usuário é o proprietário da tarefa
        if (!existingTask.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to delete this task");
        }
        
        // Remove a tarefa
        taskRepository.deleteById(taskId);
    }
    
    /**
     * Remove todas as tarefas de um usuário (usado na exclusão de conta)
     * 
     * @param userId ID do usuário
     * @throws IllegalArgumentException Se o ID do usuário é inválido
     */
    public void executeDeleteAllUserTasks(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        
        taskRepository.deleteByUserId(userId);
    }
    
    /**
     * Verifica se uma tarefa existe e pertence ao usuário
     * 
     * @param taskId ID da tarefa
     * @param userId ID do usuário
     * @return true se a tarefa existe e pertence ao usuário
     */
    public boolean canUserDeleteTask(Long taskId, Long userId) {
        if (taskId == null || taskId <= 0 || userId == null || userId <= 0) {
            return false;
        }
        
        return taskRepository.findById(taskId)
                .map(task -> task.getUserId().equals(userId))
                .orElse(false);
    }
    
    /**
     * Valida os dados de entrada para exclusão de tarefa
     * 
     * @param taskId ID da tarefa
     * @param userId ID do usuário
     * @throws IllegalArgumentException Se algum dado é inválido
     */
    private void validateInput(Long taskId, Long userId) {
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be a positive number");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
}