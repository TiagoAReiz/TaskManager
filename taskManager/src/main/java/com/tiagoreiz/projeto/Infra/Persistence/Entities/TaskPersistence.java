package com.tiagoreiz.projeto.Infra.Persistence.Entities;

import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entidade JPA que representa uma tarefa na camada de persistência.
 * Responsável pelo mapeamento objeto-relacional da entidade Task do domínio.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskPersistence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserPersistence user;
    
    /**
     * Construtor para criação de uma nova tarefa
     * 
     * @param title Título da tarefa
     * @param description Descrição da tarefa
     * @param priority Prioridade da tarefa
     * @param dueDate Data limite para conclusão
     * @param user Usuário proprietário da tarefa
     */
    public TaskPersistence(String title, String description, TaskPriority priority, 
                          LocalDateTime dueDate, UserPersistence user) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.user = user;
        this.status = TaskStatus.PENDING;
    }
    
    /**
     * Marca a tarefa como concluída
     */
    public void complete() {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Marca a tarefa como pendente
     */
    public void markAsPending() {
        this.status = TaskStatus.PENDING;
        this.completedAt = null;
    }
}