package com.tiagoreiz.projeto.Application.UseCases.Task;

import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import com.tiagoreiz.projeto.Core.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para atualização do status de tarefas.
 * Responsável pela lógica de negócio da mudança de status das tarefas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UpdateTaskStatusUseCase {
    
    private final TaskRepository taskRepository;
    
    /**
     * Atualiza o status de uma tarefa
     * 
     * @param taskId ID da tarefa a ser atualizada
     * @param newStatus Novo status da tarefa
     * @param userId ID do usuário (para verificação de propriedade)
     * @return Tarefa com status atualizado
     * @throws IllegalArgumentException Se os dados são inválidos ou tarefa não encontrada
     */
    public Task execute(Long taskId, TaskStatus newStatus, Long userId) {
        // Validações de entrada
        validateInput(taskId, newStatus, userId);
        
        // Busca a tarefa existente
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Verifica se o usuário é o proprietário da tarefa
        if (!existingTask.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to update this task");
        }
        
        // Se o status já é o mesmo, não faz nada
        if (existingTask.getStatus().equals(newStatus)) {
            return existingTask;
        }
        
        // Atualiza o status da tarefa
        updateTaskStatus(existingTask, newStatus);
        
        // Salva a tarefa atualizada
        return taskRepository.update(existingTask);
    }
    
    /**
     * Marca uma tarefa como concluída
     * 
     * @param taskId ID da tarefa
     * @param userId ID do usuário
     * @return Tarefa marcada como concluída
     * @throws IllegalArgumentException Se a tarefa não é encontrada ou usuário não autorizado
     */
    public Task markAsCompleted(Long taskId, Long userId) {
        return execute(taskId, TaskStatus.COMPLETED, userId);
    }
    
    /**
     * Marca uma tarefa como pendente
     * 
     * @param taskId ID da tarefa
     * @param userId ID do usuário
     * @return Tarefa marcada como pendente
     * @throws IllegalArgumentException Se a tarefa não é encontrada ou usuário não autorizado
     */
    public Task markAsPending(Long taskId, Long userId) {
        return execute(taskId, TaskStatus.PENDING, userId);
    }
    
    /**
     * Alterna o status de uma tarefa (PENDING <-> COMPLETED)
     * 
     * @param taskId ID da tarefa
     * @param userId ID do usuário
     * @return Tarefa com status alternado
     * @throws IllegalArgumentException Se a tarefa não é encontrada ou usuário não autorizado
     */
    public Task toggleTaskStatus(Long taskId, Long userId) {
        // Validações básicas
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be a positive number");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
        
        // Busca a tarefa existente
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Verifica se o usuário é o proprietário da tarefa
        if (!existingTask.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to update this task");
        }
        
        // Determina o novo status baseado no status atual
        TaskStatus newStatus = existingTask.getStatus() == TaskStatus.PENDING 
                ? TaskStatus.COMPLETED 
                : TaskStatus.PENDING;
        
        // Atualiza o status da tarefa
        updateTaskStatus(existingTask, newStatus);
        
        // Salva a tarefa atualizada
        return taskRepository.update(existingTask);
    }
    
    /**
     * Atualiza o status da tarefa e ajusta timestamps conforme necessário
     * 
     * @param task Tarefa a ser atualizada
     * @param newStatus Novo status
     */
    private void updateTaskStatus(Task task, TaskStatus newStatus) {
        if (newStatus == TaskStatus.COMPLETED) {
            task.complete();
        } else if (newStatus == TaskStatus.PENDING) {
            task.markAsPending();
        }
    }
    
    /**
     * Valida os dados de entrada para atualização de status
     * 
     * @param taskId ID da tarefa
     * @param newStatus Novo status
     * @param userId ID do usuário
     * @throws IllegalArgumentException Se algum dado é inválido
     */
    private void validateInput(Long taskId, TaskStatus newStatus, Long userId) {
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be a positive number");
        }
        
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
}