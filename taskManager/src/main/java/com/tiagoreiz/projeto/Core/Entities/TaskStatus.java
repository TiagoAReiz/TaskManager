package com.tiagoreiz.projeto.Core.Entities;

/**
 * Enum que representa os possíveis status de uma tarefa no sistema.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
public enum TaskStatus {
    /**
     * Tarefa criada mas ainda não concluída
     */
    PENDING,
    
    /**
     * Tarefa concluída com sucesso
     */
    COMPLETED
}