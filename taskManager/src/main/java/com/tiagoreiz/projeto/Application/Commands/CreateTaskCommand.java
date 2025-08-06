package com.tiagoreiz.projeto.Application.Commands;

import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Comando para criação de tarefas
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskCommand {
    
    @NotNull(message = "Title cannot be null")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    @NotNull(message = "Priority cannot be null")
    private TaskPriority priority;
    
    private LocalDateTime dueDate;
    
    @NotNull(message = "User ID cannot be null")
    private Long userId;
}