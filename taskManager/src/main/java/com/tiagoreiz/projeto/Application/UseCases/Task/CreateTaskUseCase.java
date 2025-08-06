package com.tiagoreiz.projeto.Application.UseCases.Task;

import com.tiagoreiz.projeto.Adapters.DTOs.TaskRequest;
import com.tiagoreiz.projeto.Application.Commands.CreateTaskCommand;
import com.tiagoreiz.projeto.Core.Common.Result;
import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Exceptions.TaskValidationException;
import com.tiagoreiz.projeto.Core.Exceptions.UserNotFoundException;
import com.tiagoreiz.projeto.Core.Repositories.TaskRepository;
import com.tiagoreiz.projeto.Core.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Caso de uso para criação de novas tarefas no sistema.
 * Responsável pela lógica de negócio da criação de tarefas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class CreateTaskUseCase {
    
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    
    /**
     * Cria uma nova tarefa no sistema usando DTO
     * 
     * @param taskRequest DTO com dados da tarefa
     * @param userId ID do usuário que está criando a tarefa
     * @return Result contendo a tarefa criada ou erro
     */
    public Result<Task, Exception> execute(TaskRequest taskRequest, Long userId) {
        
        try {
            // Valida o DTO
            Result<Void, TaskValidationException> validationResult = validateTaskRequest(taskRequest, userId);
            if (validationResult.isFailure()) {
                return Result.failure(validationResult.getError().get());
            }
            
            // Verifica se o usuário existe
            if (!userRepository.findById(userId).isPresent()) {
                return Result.failure(new UserNotFoundException(userId));
            }
            
            // Valida a data de vencimento se fornecida
            if (taskRequest.getDueDate() != null && taskRequest.getDueDate().isBefore(LocalDateTime.now())) {
                return Result.failure(new TaskValidationException("Due date cannot be in the past"));
            }
            
            // Cria a entidade Task
            Task task = mapToTask(taskRequest, userId);
            
            // Salva a tarefa
            Task savedTask = taskRepository.save(task);
            return Result.success(savedTask);
            
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    /**
     * Cria uma nova tarefa no sistema usando comando (método legado)
     * 
     * @param command Comando contendo todos os dados necessários para criação
     * @return Result contendo a tarefa criada ou erro
     */
    public Result<Task, Exception> execute(CreateTaskCommand command) {
        
        try {
            // Validações de entrada
            Result<Void, TaskValidationException> validationResult = validateCommand(command);
            if (validationResult.isFailure()) {
                return Result.failure(validationResult.getError().get());
            }
            
            // Verifica se o usuário existe
            if (!userRepository.findById(command.getUserId()).isPresent()) {
                return Result.failure(new UserNotFoundException(command.getUserId()));
            }
            
            // Valida a data de vencimento se fornecida
            if (command.getDueDate() != null && command.getDueDate().isBefore(LocalDateTime.now())) {
                return Result.failure(new TaskValidationException("Due date cannot be in the past"));
            }
            
            // Cria a entidade Task
            Task task = new Task(command.getTitle(), command.getDescription(), 
                               command.getPriority(), command.getDueDate(), command.getUserId());
            
            // Salva a tarefa
            Task savedTask = taskRepository.save(task);
            return Result.success(savedTask);
            
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    /**
     * Método de compatibilidade para manter a interface original
     * @deprecated Use execute(CreateTaskCommand) instead
     */
    @Deprecated
    public Task execute(String title, String description, TaskPriority priority, 
                       LocalDateTime dueDate, Long userId) {
        CreateTaskCommand command = new CreateTaskCommand(title, description, priority, dueDate, userId);
        Result<Task, Exception> result = execute(command);
        if (result.isSuccess()) {
            return result.getValueOrThrow();
        } else {
            throw new RuntimeException(result.getError().get());
        }
    }
    
    /**
     * Valida os dados de entrada do comando
     * 
     * @param command Comando a ser validado
     * @return Result indicando sucesso ou erro de validação
     */
    private Result<Void, TaskValidationException> validateCommand(CreateTaskCommand command) {
        if (command == null) {
            return Result.failure(new TaskValidationException("Command cannot be null"));
        }
        
        if (command.getTitle() == null || command.getTitle().trim().isEmpty()) {
            return Result.failure(new TaskValidationException("Title cannot be null or empty"));
        }
        
        if (command.getTitle().length() < 3 || command.getTitle().length() > 200) {
            return Result.failure(new TaskValidationException("Title must be between 3 and 200 characters"));
        }
        
        if (command.getDescription() != null && command.getDescription().length() > 2000) {
            return Result.failure(new TaskValidationException("Description must not exceed 2000 characters"));
        }
        
        if (command.getPriority() == null) {
            return Result.failure(new TaskValidationException("Priority cannot be null"));
        }
        
        if (command.getUserId() == null) {
            return Result.failure(new TaskValidationException("User ID cannot be null"));
        }
        
        if (command.getUserId() <= 0) {
            return Result.failure(new TaskValidationException("User ID must be a positive number"));
        }
        
        return Result.success(null);
    }
    
    /**
     * Valida os dados de entrada do DTO
     * 
     * @param taskRequest DTO a ser validado
     * @param userId ID do usuário
     * @return Result indicando sucesso ou erro de validação
     */
    private Result<Void, TaskValidationException> validateTaskRequest(TaskRequest taskRequest, Long userId) {
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
        
        if (userId == null) {
            return Result.failure(new TaskValidationException("User ID cannot be null"));
        }
        
        if (userId <= 0) {
            return Result.failure(new TaskValidationException("User ID must be a positive number"));
        }
        
        return Result.success(null);
    }
    
    /**
     * Mapeia um TaskRequest DTO para uma entidade Task
     * 
     * @param taskRequest DTO com dados da tarefa
     * @param userId ID do usuário
     * @return Entidade Task
     */
    private Task mapToTask(TaskRequest taskRequest, Long userId) {
        return new Task(
            taskRequest.getTitle(),
            taskRequest.getDescription(),
            taskRequest.getPriority(),
            taskRequest.getDueDate(),
            userId
        );
    }
}