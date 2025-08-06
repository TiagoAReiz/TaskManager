package com.tiagoreiz.projeto.Adapters.DTOs;

import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para requisições relacionadas a tarefas (criação e atualização).
 * Contém as validações necessárias para os dados de entrada.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    
    /**
     * Título da tarefa
     */
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    
    /**
     * Descrição da tarefa (opcional)
     */
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    /**
     * Prioridade da tarefa
     */
    @NotNull(message = "Priority is required")
    private TaskPriority priority;
    
    /**
     * Data limite para conclusão da tarefa (opcional)
     */
    private LocalDateTime dueDate;
}