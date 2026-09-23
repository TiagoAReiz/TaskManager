package com.tiagoreiz.projeto.Core.Entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TaskTest {

    @Test
    @DisplayName("Nova tarefa começa PENDING, com timestamps e sem data de conclusão")
    void newTaskStartsPending() {
        Task task = new Task("Estudar", "Clean Architecture", TaskPriority.HIGH, null, 1L);

        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(task.getCreatedAt()).isNotNull();
        assertThat(task.getUpdatedAt()).isNotNull();
        assertThat(task.getCompletedAt()).isNull();
        assertThat(task.getUserId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("complete() marca COMPLETED e registra completedAt")
    void completeSetsStatusAndCompletedAt() {
        Task task = new Task("Estudar", null, TaskPriority.LOW, null, 1L);

        task.complete();

        assertThat(task.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(task.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("markAsPending() reabre a tarefa e limpa completedAt")
    void markAsPendingClearsCompletedAt() {
        Task task = new Task("Estudar", null, TaskPriority.LOW, null, 1L);
        task.complete();

        task.markAsPending();

        assertThat(task.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(task.getCompletedAt()).isNull();
    }

    @Test
    @DisplayName("isOverdue() só é verdadeiro para tarefa pendente com prazo vencido")
    void isOverdueRules() {
        Task late = new Task("Atrasada", null, TaskPriority.MEDIUM, LocalDateTime.now().minusDays(1), 1L);
        Task future = new Task("Futura", null, TaskPriority.MEDIUM, LocalDateTime.now().plusDays(1), 1L);
        Task noDueDate = new Task("Sem prazo", null, TaskPriority.MEDIUM, null, 1L);
        Task lateButDone = new Task("Concluída", null, TaskPriority.MEDIUM, LocalDateTime.now().minusDays(1), 1L);
        lateButDone.complete();

        assertThat(late.isOverdue()).isTrue();
        assertThat(future.isOverdue()).isFalse();
        assertThat(noDueDate.isOverdue()).isFalse();
        assertThat(lateButDone.isOverdue()).isFalse();
    }
}
