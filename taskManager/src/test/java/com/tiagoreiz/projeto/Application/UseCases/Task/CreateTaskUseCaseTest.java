package com.tiagoreiz.projeto.Application.UseCases.Task;

import com.tiagoreiz.projeto.Adapters.DTOs.TaskRequest;
import com.tiagoreiz.projeto.Core.Common.Result;
import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import com.tiagoreiz.projeto.Core.Entities.User;
import com.tiagoreiz.projeto.Core.Exceptions.TaskValidationException;
import com.tiagoreiz.projeto.Core.Exceptions.UserNotFoundException;
import com.tiagoreiz.projeto.Core.Repositories.TaskRepository;
import com.tiagoreiz.projeto.Core.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTaskUseCaseTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateTaskUseCase useCase;

    @Test
    void createsPendingTaskForExistingUser() {
        LocalDateTime due = LocalDateTime.now().plusDays(3);
        TaskRequest request = new TaskRequest("Escrever testes", "Casos de uso", TaskPriority.HIGH, due);
        when(userRepository.findById(7L)).thenReturn(Optional.of(new User("Ana", "ana@mail.com", "hash")));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> {
            Task t = inv.getArgument(0);
            t.setId(100L);
            return t;
        });

        Result<Task, Exception> result = useCase.execute(request, 7L);

        assertThat(result.isSuccess()).isTrue();
        ArgumentCaptor<Task> saved = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(saved.capture());
        assertThat(saved.getValue().getTitle()).isEqualTo("Escrever testes");
        assertThat(saved.getValue().getUserId()).isEqualTo(7L);
        assertThat(saved.getValue().getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(result.getValueOrThrow().getId()).isEqualTo(100L);
    }

    @Test
    void rejectsTitleShorterThanThreeCharacters() {
        TaskRequest request = new TaskRequest("ab", null, TaskPriority.LOW, null);

        Result<Task, Exception> result = useCase.execute(request, 1L);

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getError().get()).isInstanceOf(TaskValidationException.class);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void failsWhenUserDoesNotExist() {
        TaskRequest request = new TaskRequest("Tarefa valida", null, TaskPriority.LOW, null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Result<Task, Exception> result = useCase.execute(request, 99L);

        assertThat(result.getError().get()).isInstanceOf(UserNotFoundException.class);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void rejectsDueDateInThePast() {
        TaskRequest request = new TaskRequest("Tarefa valida", null, TaskPriority.LOW, LocalDateTime.now().minusHours(1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User("Ana", "ana@mail.com", "hash")));

        Result<Task, Exception> result = useCase.execute(request, 1L);

        assertThat(result.getError().get())
                .isInstanceOf(TaskValidationException.class)
                .hasMessageContaining("past");
        verify(taskRepository, never()).save(any());
    }
}
