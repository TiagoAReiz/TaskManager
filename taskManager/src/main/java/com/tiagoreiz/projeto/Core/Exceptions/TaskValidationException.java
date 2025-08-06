package com.tiagoreiz.projeto.Core.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando há erros de validação em tarefas
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
public class TaskValidationException extends BusinessException {
    
    private static final String ERROR_CODE = "TASK_VALIDATION_ERROR";
    
    public TaskValidationException(String message) {
        super(message, ERROR_CODE, HttpStatus.BAD_REQUEST);
    }
    
    public TaskValidationException(String field, String violation) {
        super(
            String.format("Validation error on field '%s': %s", field, violation),
            ERROR_CODE,
            HttpStatus.BAD_REQUEST
        );
    }
}