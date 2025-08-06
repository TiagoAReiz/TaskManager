package com.tiagoreiz.projeto.Core.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade de domínio que representa uma tarefa no sistema TaskMaster.
 * Esta é uma entidade pura da camada Core, sem dependências externas.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    
    /**
     * Identificador único da tarefa
     */
    private Long id;
    
    /**
     * Título da tarefa
     */
    private String title;
    
    /**
     * Descrição detalhada da tarefa
     */
    private String description;
    
    /**
     * Status atual da tarefa (PENDING ou COMPLETED)
     */
    private TaskStatus status;
    
    /**
     * Prioridade da tarefa (LOW, MEDIUM ou HIGH)
     */
    private TaskPriority priority;
    
    /**
     * Data e hora de criação da tarefa
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização da tarefa
     */
    private LocalDateTime updatedAt;
    
    /**
     * Data limite para conclusão da tarefa
     */
    private LocalDateTime dueDate;
    
    /**
     * Data e hora em que a tarefa foi concluída
     */
    private LocalDateTime completedAt;
    
    /**
     * ID do usuário proprietário da tarefa
     */
    private Long userId;
    
    /**
     * Construtor para criação de uma nova tarefa
     * 
     * @param title Título da tarefa
     * @param description Descrição da tarefa
     * @param priority Prioridade da tarefa
     * @param dueDate Data limite para conclusão
     * @param userId ID do usuário proprietário
     */
    public Task(String title, String description, TaskPriority priority, LocalDateTime dueDate, Long userId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.userId = userId;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marca a tarefa como concluída
     */
    public void complete() {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Marca a tarefa como pendente
     */
    public void markAsPending() {
        this.status = TaskStatus.PENDING;
        this.completedAt = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Atualiza o timestamp de última modificação
     */
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Verifica se a tarefa está atrasada
     * 
     * @return true se a tarefa está pendente e passou da data limite
     */
    public boolean isOverdue() {
        return status == TaskStatus.PENDING && 
               dueDate != null && 
               LocalDateTime.now().isAfter(dueDate);
    }
}