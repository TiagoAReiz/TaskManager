package com.tiagoreiz.projeto.Core.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando um usuário não é encontrado
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
public class UserNotFoundException extends BusinessException {
    
    private static final String ERROR_CODE = "USER_NOT_FOUND";
    
    public UserNotFoundException(Long userId) {
        super(
            String.format("User with ID %d not found", userId),
            ERROR_CODE,
            HttpStatus.NOT_FOUND
        );
    }
    
    public UserNotFoundException(String email) {
        super(
            String.format("User with email %s not found", email),
            ERROR_CODE,
            HttpStatus.NOT_FOUND
        );
    }
    
    public UserNotFoundException(String message, boolean isCustomMessage) {
        super(message, ERROR_CODE, HttpStatus.NOT_FOUND);
    }
}