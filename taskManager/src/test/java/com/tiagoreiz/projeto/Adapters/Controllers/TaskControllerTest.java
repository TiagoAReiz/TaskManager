package com.tiagoreiz.projeto.Adapters.Controllers;

import com.tiagoreiz.projeto.Adapters.DTOs.TaskRequest;
import com.tiagoreiz.projeto.Adapters.Mappers.TaskMapperImpl;
import com.tiagoreiz.projeto.Application.UseCases.Task.CreateTaskUseCase;
import com.tiagoreiz.projeto.Application.UseCases.Task.DeleteTaskUseCase;
import com.tiagoreiz.projeto.Application.UseCases.Task.GetUserTasksUseCase;
import com.tiagoreiz.projeto.Application.UseCases.Task.UpdateTaskStatusUseCase;
import com.tiagoreiz.projeto.Application.UseCases.Task.UpdateTaskUseCase;
import com.tiagoreiz.projeto.Core.Common.Result;
import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Infra.Security.JwtService;
import com.tiagoreiz.projeto.Infra.Security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de fatia web: sobe o TaskController com a configuração real de segurança
 * (SecurityConfig + JwtAuthenticationFilter) e o mapper MapStruct gerado,
 * mockando apenas os casos de uso e a validação do JWT.
 */
@WebMvcTest(TaskController.class)
@Import({SecurityConfig.class, TaskMapperImpl.class})
class TaskControllerTest {

    private static final String TOKEN = "valid-token";
    private static final String BEARER = "Bearer " + TOKEN;
    private static final long USER_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockBean private CreateTaskUseCase createTaskUseCase;
    @MockBean private GetUserTasksUseCase getUserTasksUseCase;
    @MockBean private UpdateTaskUseCase updateTaskUseCase;
    @MockBean private UpdateTaskStatusUseCase updateTaskStatusUseCase;
    @MockBean private DeleteTaskUseCase deleteTaskUseCase;
    @MockBean private JwtService jwtService;
    @MockBean private UserDetailsService userDetailsService;

    @BeforeEach
    void authenticateToken() {
        User principal = new User("ana@mail.com", "hash", List.of());
        when(jwtService.extractUsername(TOKEN)).thenReturn("ana@mail.com");
        when(jwtService.extractUserId(TOKEN)).thenReturn(USER_ID);
        when(jwtService.isTokenValid(eq(TOKEN), any())).thenReturn(true);
        when(userDetailsService.loadUserByUsername("ana@mail.com")).thenReturn(principal);
    }

    private static Task task(long id, String title) {
        Task task = new Task(title, null, TaskPriority.HIGH, null, USER_ID);
        task.setId(id);
        return task;
    }

    @Test
    void requestWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(getUserTasksUseCase);
    }

    @Test
    void listsTasksOfAuthenticatedUser() throws Exception {
        when(getUserTasksUseCase.execute(USER_ID)).thenReturn(List.of(task(1L, "Primeira"), task(2L, "Segunda")));

        mockMvc.perform(get("/api/tasks").header("Authorization", BEARER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Primeira"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void createsTaskAndReturns201() throws Exception {
        when(createTaskUseCase.execute(any(TaskRequest.class), eq(USER_ID)))
                .thenReturn(Result.success(task(10L, "Nova tarefa")));

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", BEARER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Nova tarefa\",\"priority\":\"HIGH\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Nova tarefa"));
    }

    @Test
    void invalidPayloadReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", BEARER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.validationErrors").isArray());

        verifyNoInteractions(createTaskUseCase);
    }

    @Test
    void deletingTaskOfAnotherUserReturns400() throws Exception {
        doThrow(new IllegalArgumentException("User is not authorized to delete this task"))
                .when(deleteTaskUseCase).execute(99L, USER_ID);

        mockMvc.perform(delete("/api/tasks/99").header("Authorization", BEARER))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_ARGUMENT"));
    }

    @Test
    void deletesOwnTaskAndReturns204() throws Exception {
        mockMvc.perform(delete("/api/tasks/5").header("Authorization", BEARER))
                .andExpect(status().isNoContent());

        verify(deleteTaskUseCase).execute(5L, USER_ID);
    }
}
