package com.tiagoreiz.projeto.Core.Common;

import java.util.Optional;
import java.util.function.Function;

/**
 * Classe para encapsular resultados de operações que podem falhar
 * Evita o uso de exceções para controle de fluxo
 * 
 * @param <T> Tipo do valor de sucesso
 * @param <E> Tipo do erro
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
public class Result<T, E> {
    
    private final T value;
    private final E error;
    private final boolean isSuccess;
    
    private Result(T value, E error, boolean isSuccess) {
        this.value = value;
        this.error = error;
        this.isSuccess = isSuccess;
    }
    
    /**
     * Cria um resultado de sucesso
     */
    public static <T, E> Result<T, E> success(T value) {
        return new Result<>(value, null, true);
    }
    
    /**
     * Cria um resultado de erro
     */
    public static <T, E> Result<T, E> failure(E error) {
        return new Result<>(null, error, false);
    }
    
    /**
     * Verifica se o resultado é um sucesso
     */
    public boolean isSuccess() {
        return isSuccess;
    }
    
    /**
     * Verifica se o resultado é um erro
     */
    public boolean isFailure() {
        return !isSuccess;
    }
    
    /**
     * Obtém o valor (apenas se for sucesso)
     */
    public Optional<T> getValue() {
        return isSuccess ? Optional.of(value) : Optional.empty();
    }
    
    /**
     * Obtém o erro (apenas se for falha)
     */
    public Optional<E> getError() {
        return isSuccess ? Optional.empty() : Optional.of(error);
    }
    
    /**
     * Obtém o valor ou lança exceção se for erro
     */
    public T getValueOrThrow() {
        if (isSuccess) {
            return value;
        }
        throw new RuntimeException("Tentativa de obter valor de um resultado com erro: " + error);
    }
    
    /**
     * Mapeia o valor para outro tipo se for sucesso
     */
    public <U> Result<U, E> map(Function<T, U> mapper) {
        if (isSuccess) {
            return Result.success(mapper.apply(value));
        }
        return Result.failure(error);
    }
    
    /**
     * Mapeia o erro para outro tipo se for falha
     */
    public <F> Result<T, F> mapError(Function<E, F> mapper) {
        if (isFailure()) {
            return Result.failure(mapper.apply(error));
        }
        return Result.success(value);
    }
    
    /**
     * Executa uma ação se for sucesso
     */
    public Result<T, E> onSuccess(java.util.function.Consumer<T> action) {
        if (isSuccess) {
            action.accept(value);
        }
        return this;
    }
    
    /**
     * Executa uma ação se for erro
     */
    public Result<T, E> onFailure(java.util.function.Consumer<E> action) {
        if (isFailure()) {
            action.accept(error);
        }
        return this;
    }
}