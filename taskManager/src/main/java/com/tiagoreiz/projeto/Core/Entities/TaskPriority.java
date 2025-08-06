package com.tiagoreiz.projeto.Core.Entities;

/**
 * Enum que representa os possíveis níveis de prioridade de uma tarefa.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
public enum TaskPriority {
    /**
     * Prioridade baixa - tarefas que podem ser feitas quando há tempo disponível
     */
    LOW,
    
    /**
     * Prioridade média - tarefas que devem ser feitas em tempo hábil
     */
    MEDIUM,
    
    /**
     * Prioridade alta - tarefas urgentes que devem ser priorizadas
     */
    HIGH
}