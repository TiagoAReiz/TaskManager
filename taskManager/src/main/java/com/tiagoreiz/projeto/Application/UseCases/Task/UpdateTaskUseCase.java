package com.tiagoreiz.projeto.Application.UseCases.Task;

import com.tiagoreiz.projeto.Adapters.DTOs.TaskRequest;
import com.tiagoreiz.projeto.Core.Common.Result;
import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Exceptions.TaskNotFoundException;
import com.tiagoreiz.projeto.Core.Exceptions.TaskValidationException;
import com.tiagoreiz.projeto.Core.Repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Caso de uso para atualização de tarefas existentes.
 * Responsável pela lógica de negócio da atualização de tarefas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UpdateTaskUseCase {
    
    private final TaskRepository taskRepository;
    
    /**
     * Atualiza uma tarefa existente usando DTO
     * 
     * @param taskId ID da tarefa a ser atualizada
     * @param taskRequest DTO com os novos dados da tarefa
     * @param userId ID do usuário (para verificação de propriedade)
     * @return Result contendo a tarefa atualizada ou erro
     */
    public Result<Task, Exception> execute(Long taskId, TaskRequest taskRequest, Long userId) {
        
        try {
            // Validações de entrada
            Result<Void, TaskValidationException> validationResult = validateTaskRequest(taskId, taskRequest, userId);
            if (validationResult.isFailure()) {
                return Result.failure(validationResult.getError().get());
            }
            
            // Busca a tarefa existente
            Task existingTask = taskRepository.findById(taskId)
                    .orElseThrow(() -> new TaskNotFoundException(taskId));
            
            // Verifica se o usuário é o proprietário da tarefa
            if (!existingTask.getUserId().equals(userId)) {
                return Result.failure(new TaskValidationException("User is not authorized to update this task"));
            }
            
            // Valida a data de vencimento se fornecida
            if (taskRequest.getDueDate() != null && taskRequest.getDueDate().isBefore(LocalDateTime.now())) {
                return Result.failure(new TaskValidationException("Due date cannot be in the past"));
            }
            
            // Atualiza a tarefa
            updateTaskFromRequest(existingTask, taskRequest);
            
            // Salva a tarefa atualizada
            Task updatedTask = taskRepository.save(existingTask);
            return Result.success(updatedTask);
            
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    /**
     * Atualiza uma tarefa existente (método legado)
     * 
     * @param taskId ID da tarefa a ser atualizada
     * @param title Novo título da tarefa
     * @param description Nova descrição da tarefa
     * @param priority Nova prioridade da tarefa
     * @param dueDate Nova data limite (opcional)
     * @param userId ID do usuário (para verificação de propriedade)
     * @return Tarefa atualizada
     * @throws IllegalArgumentException Se os dados são inválidos ou tarefa não encontrada
     */
    public Task execute(Long taskId, String title, String description, 
                       TaskPriority priority, LocalDateTime dueDate, Long userId) {
        
        // Validações de entrada
        validateInput(taskId, title, description, priority, userId);
        
        // Busca a tarefa existente
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Verifica se o usuário é o proprietário da tarefa
        if (!existingTask.getUserId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to update this task");
        }
        
        // Valida a data de vencimento se fornecida
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        
        // Atualiza os campos da tarefa
        existingTask.setTitle(title);
        existingTask.setDescription(description);
        existingTask.setPriority(priority);
        existingTask.setDueDate(dueDate);
        existingTask.updateTimestamp();
        
        // Salva a tarefa atualizada
        return taskRepository.update(existingTask);
    }
    
    /**
     * Atualiza apenas campos específicos de uma tarefa
     * 
     * @param taskId ID da tarefa a ser atualizada
     * @param userId ID do usuário (para verificação de propriedade)
     * @param title Novo título (se não for null)
     * @param description Nova descrição (se não for null)
     * @param priority Nova prioridade (se não for null)
     * @param dueDate Nova data limite (se não for null)
     * @return Tarefa atualizada
     * @throws IllegalArgumentException Se a tarefa não é encontrada ou usuário não autorizado
     */
    public Task executePartialUpdate(Long taskId, Long userId, String title, 
                                   String description, TaskPriority priority, LocalDateTime dueDate) {
        
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
        
        // Atualiza apenas os campos fornecidos
        boolean hasChanges = false;
        
        if (title != null && !title.trim().isEmpty()) {
            if (title.length() < 3 || title.length() > 200) {
                throw new IllegalArgumentException("Title must be between 3 and 200 characters");
            }
            existingTask.setTitle(title);
            hasChanges = true;
        }
        
        if (description != null) {
            if (description.length() > 2000) {
                throw new IllegalArgumentException("Description must not exceed 2000 characters");
            }
            existingTask.setDescription(description);
            hasChanges = true;
        }
        
        if (priority != null) {
            existingTask.setPriority(priority);
            hasChanges = true;
        }
        
        if (dueDate != null) {
            if (dueDate.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Due date cannot be in the past");
            }
            existingTask.setDueDate(dueDate);
            hasChanges = true;
        }
        
        if (hasChanges) {
            existingTask.updateTimestamp();
            return taskRepository.update(existingTask);
        }
        
        return existingTask;
    }
    
    /**
     * Valida os dados de entrada para atualização de tarefa
     * 
     * @param taskId ID da tarefa
     * @param title Título da tarefa
     * @param description Descrição da tarefa
     * @param priority Prioridade da tarefa
     * @param userId ID do usuário
     * @throws IllegalArgumentException Se algum dado é inválido
     */
    private void validateInput(Long taskId, String title, String description, 
                              TaskPriority priority, Long userId) {
        
        if (taskId == null || taskId <= 0) {
            throw new IllegalArgumentException("Task ID must be a positive number");
        }
        
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        
        if (title.length() < 3 || title.length() > 200) {
            throw new IllegalArgumentException("Title must be between 3 and 200 characters");
        }
        
        if (description != null && description.length() > 2000) {
            throw new IllegalArgumentException("Description must not exceed 2000 characters");
        }
        
        if (priority == null) {
            throw new IllegalArgumentException("Priority cannot be null");
        }
        
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("User ID must be a positive number");
        }
    }
    
    /**
     * Valida os dados de entrada do DTO para atualização
     * 
     * @param taskId ID da tarefa
     * @param taskRequest DTO a ser validado
     * @param userId ID do usuário
     * @return Result indicando sucesso ou erro de validação
     */
    private Result<Void, TaskValidationException> validateTaskRequest(Long taskId, TaskRequest taskRequest, Long userId) {
        if (taskId == null || taskId <= 0) {
            return Result.failure(new TaskValidationException("Task ID must be a positive number"));
        }
        
        if (taskRequest == null) {
            return Result.failure(new TaskValidationException("Task request cannot be null"));
        }
        
        if (taskRequest.getTitle() == null || taskRequest.getTitle().trim().isEmpty()) {
            return Result.failure(new TaskValidationException("Title cannot be null or empty"));
        }
        
        if (taskRequest.getTitle().length() < 3 || taskRequest.getTitle().length() > 200) {
            return Result.failure(new TaskValidationException("Title must be between 3 and 200 characters"));
        }
        
        if (taskRequest.getDescription() != null && taskRequest.getDescription().length() > 2000) {
            return Result.failure(new TaskValidationException("Description must not exceed 2000 characters"));
        }
        
        if (taskRequest.getPriority() == null) {
            return Result.failure(new TaskValidationException("Priority cannot be null"));
        }
        
        if (userId == null || userId <= 0) {
            return Result.failure(new TaskValidationException("User ID must be a positive number"));
        }
        
        return Result.success(null);
    }
    
    /**
     * Atualiza uma tarefa com os dados do DTO
     * 
     * @param task Tarefa a ser atualizada
     * @param taskRequest DTO com os novos dados
     */
    private void updateTaskFromRequest(Task task, TaskRequest taskRequest) {
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setPriority(taskRequest.getPriority());
        task.setDueDate(taskRequest.getDueDate());
        task.updateTimestamp();
    }
}