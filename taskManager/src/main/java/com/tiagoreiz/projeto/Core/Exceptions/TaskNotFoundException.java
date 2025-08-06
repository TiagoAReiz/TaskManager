package com.tiagoreiz.projeto.Core.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando uma tarefa não é encontrada
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
public class TaskNotFoundException extends BusinessException {
    
    private static final String ERROR_CODE = "TASK_NOT_FOUND";
    
    public TaskNotFoundException(Long taskId) {
        super(
            String.format("Task with ID %d not found", taskId),
            ERROR_CODE,
            HttpStatus.NOT_FOUND
        );
    }
    
    public TaskNotFoundException(String message) {
        super(message, ERROR_CODE, HttpStatus.NOT_FOUND);
    }
}