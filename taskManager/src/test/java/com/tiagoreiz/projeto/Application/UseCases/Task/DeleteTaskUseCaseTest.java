package com.tiagoreiz.projeto.Application.UseCases.Task;

import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Repositories.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private DeleteTaskUseCase useCase;

    private Task taskOwnedBy(long userId) {
        Task task = new Task("Tarefa", null, TaskPriority.LOW, null, userId);
        task.setId(5L);
        return task;
    }

    @Test
    void ownerDeletesTask() {
        when(taskRepository.findById(5L)).thenReturn(Optional.of(taskOwnedBy(1L)));

        useCase.execute(5L, 1L);

        verify(taskRepository).deleteById(5L);
    }

    @Test
    void nonOwnerCannotDeleteTask() {
        when(taskRepository.findById(5L)).thenReturn(Optional.of(taskOwnedBy(1L)));

        assertThatThrownBy(() -> useCase.execute(5L, 2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not authorized");
        verify(taskRepository, never()).deleteById(anyLong());
    }

    @Test
    void canUserDeleteTaskReflectsOwnership() {
        when(taskRepository.findById(5L)).thenReturn(Optional.of(taskOwnedBy(1L)));

        assertThat(useCase.canUserDeleteTask(5L, 1L)).isTrue();
        assertThat(useCase.canUserDeleteTask(5L, 2L)).isFalse();
        assertThat(useCase.canUserDeleteTask(null, 1L)).isFalse();
    }
}
