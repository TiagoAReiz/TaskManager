package com.tiagoreiz.projeto.Application.UseCases.Task;

import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import com.tiagoreiz.projeto.Core.Repositories.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTaskStatusUseCaseTest {

    private static final long OWNER_ID = 1L;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UpdateTaskStatusUseCase useCase;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("Tarefa", null, TaskPriority.MEDIUM, null, OWNER_ID);
        task.setId(10L);
    }

    @Test
    void ownerCanCompleteTask() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.update(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task updated = useCase.execute(10L, TaskStatus.COMPLETED, OWNER_ID);

        assertThat(updated.getStatus()).isEqualTo(TaskStatus.COMPLETED);
        assertThat(updated.getCompletedAt()).isNotNull();
    }

    @Test
    void otherUserCannotChangeStatus() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> useCase.execute(10L, TaskStatus.COMPLETED, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not authorized");
        verify(taskRepository, never()).update(any());
    }

    @Test
    void sameStatusIsANoOp() {
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        Task result = useCase.execute(10L, TaskStatus.PENDING, OWNER_ID);

        assertThat(result).isSameAs(task);
        verify(taskRepository, never()).update(any());
    }

    @Test
    void missingTaskIsReported() {
        when(taskRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(10L, TaskStatus.COMPLETED, OWNER_ID))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void toggleFlipsCompletedBackToPending() {
        task.complete();
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.update(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task toggled = useCase.toggleTaskStatus(10L, OWNER_ID);

        assertThat(toggled.getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(toggled.getCompletedAt()).isNull();
    }

    @Test
    void rejectsInvalidInputBeforeHittingRepository() {
        assertThatThrownBy(() -> useCase.execute(0L, TaskStatus.COMPLETED, OWNER_ID))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.execute(10L, null, OWNER_ID))
                .isInstanceOf(IllegalArgumentException.class);
        verify(taskRepository, never()).findById(any());
    }
}
