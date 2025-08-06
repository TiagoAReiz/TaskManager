package com.tiagoreiz.projeto.Adapters.DTOs;

import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para resposta de tarefas.
 * Contém todas as informações da tarefa formatadas para retorno via API.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    
    /**
     * ID único da tarefa
     */
    private Long id;
    
    /**
     * Título da tarefa
     */
    private String title;
    
    /**
     * Descrição da tarefa
     */
    private String description;
    
    /**
     * Status atual da tarefa
     */
    private TaskStatus status;
    
    /**
     * Prioridade da tarefa
     */
    private TaskPriority priority;
    
    /**
     * Data e hora de criação da tarefa
     */
    private LocalDateTime createdAt;
    
    /**
     * Data e hora da última atualização
     */
    private LocalDateTime updatedAt;
    
    /**
     * Data limite para conclusão
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
     * Indica se a tarefa está atrasada
     */
    private Boolean isOverdue;
    
    /**
     * Número de dias restantes até o vencimento (negativo se atrasada)
     */
    private Long daysUntilDue;
    
    /**
     * Construtor principal sem campos calculados
     * Os campos isOverdue e daysUntilDue serão calculados separadamente
     */
    public TaskResponse(Long id, String title, String description, TaskStatus status, 
                       TaskPriority priority, LocalDateTime createdAt, LocalDateTime updatedAt,
                       LocalDateTime dueDate, LocalDateTime completedAt, Long userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.dueDate = dueDate;
        this.completedAt = completedAt;
        this.userId = userId;
        
        // Calcula campos derivados
        calculateDerivedFields();
    }
    
    /**
     * Calcula campos derivados como isOverdue e daysUntilDue
     */
    private void calculateDerivedFields() {
        if (dueDate != null && status == TaskStatus.PENDING) {
            LocalDateTime now = LocalDateTime.now();
            this.isOverdue = now.isAfter(dueDate);
            
            // Calcula dias até o vencimento
            long hoursDiff = java.time.Duration.between(now, dueDate).toHours();
            this.daysUntilDue = hoursDiff / 24;
        } else {
            this.isOverdue = false;
            this.daysUntilDue = null;
        }
    }
    
    /**
     * Atualiza os campos derivados (usado após mudanças no status ou dueDate)
     */
    public void updateDerivedFields() {
        calculateDerivedFields();
    }
}