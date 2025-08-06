package com.tiagoreiz.projeto.Core.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção lançada quando as credenciais são inválidas
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
public class InvalidCredentialsException extends BusinessException {
    
    private static final String ERROR_CODE = "INVALID_CREDENTIALS";
    
    public InvalidCredentialsException() {
        super(
            "Invalid email or password",
            ERROR_CODE,
            HttpStatus.UNAUTHORIZED
        );
    }
    
    public InvalidCredentialsException(String message) {
        super(message, ERROR_CODE, HttpStatus.UNAUTHORIZED);
    }
}