package com.tiagoreiz.projeto.Core.Exceptions;

import org.springframework.http.HttpStatus;

/**
 * Exceção genérica para recursos não encontrados (entidades).
 * Pode ser utilizada para representar a ausência de qualquer recurso identificado por ID (ou outro identificador).
 *
 * @author Tiago Reiz
 * @version 1.0
 */
public class ResourceNotFoundException extends BusinessException {

    private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("%s with ID %s not found", resourceName, identifier), ERROR_CODE, HttpStatus.NOT_FOUND);
    }

    public ResourceNotFoundException(String message) {
        super(message, ERROR_CODE, HttpStatus.NOT_FOUND);
    }
}
