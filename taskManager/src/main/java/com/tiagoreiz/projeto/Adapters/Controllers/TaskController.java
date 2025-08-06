package com.tiagoreiz.projeto.Adapters.Controllers;

import com.tiagoreiz.projeto.Adapters.DTOs.TaskRequest;
import com.tiagoreiz.projeto.Adapters.DTOs.TaskResponse;
import com.tiagoreiz.projeto.Adapters.Mappers.TaskMapper;
import com.tiagoreiz.projeto.Application.UseCases.Task.*;
import com.tiagoreiz.projeto.Core.Common.Result;
import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import com.tiagoreiz.projeto.Core.Exceptions.TaskNotFoundException;
import com.tiagoreiz.projeto.Infra.Security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pelos endpoints de gerenciamento de tarefas.
 * Expõe APIs RESTful para CRUD completo de tarefas.
 * 
 * @author Tiago Reiz
 * @version 2.0 - Refatorado para usar tratamento centralizado de erros
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tasks", description = "Endpoints para gerenciamento de tarefas")
@SecurityRequirement(name = "bearer-jwt")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final GetUserTasksUseCase getUserTasksUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final UpdateTaskStatusUseCase updateTaskStatusUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final TaskMapper taskMapper;
    private final JwtService jwtService;

    /**
     * Extrai o ID do usuário do token JWT
     */
    private Long extractUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header is required");
        }
        
        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwt);
        return jwtService.extractUserId(jwt);
    }

    /**
     * Cria uma nova tarefa
     */
    @PostMapping
    @Operation(summary = "Criar nova tarefa", description = "Cria uma nova tarefa para o usuário autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest, 
                                                   HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        
        log.info("Creating task for user {}: {}", userId, taskRequest.getTitle());
        
        Result<Task, Exception> result = createTaskUseCase.execute(taskRequest, userId);
        
        if (result.isSuccess()) {
            Task task = result.getValueOrThrow();
            TaskResponse response = taskMapper.toResponse(task);
            
            log.info("Task created successfully with ID: {}", task.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            // O GlobalExceptionHandler irá tratar a exceção automaticamente
            Exception error = result.getError().get();
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            } else {
                throw new RuntimeException(error);
            }
        }
    }

    /**
     * Lista tarefas com filtros opcionais
     */
    @GetMapping
    @Operation(summary = "Listar tarefas", description = "Lista todas as tarefas do usuário autenticado com filtros opcionais")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tarefas retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<List<TaskResponse>> getTasks(
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Filtrar por prioridade") @RequestParam(required = false) TaskPriority priority,
            HttpServletRequest request) {
        
        Long userId = extractUserIdFromToken(request);
        
        List<Task> tasks;
        
        if (status != null && priority != null) {
            tasks = getUserTasksUseCase.executeByStatusAndPriority(userId, status, priority);
        } else if (status != null) {
            tasks = getUserTasksUseCase.executeByStatus(userId, status);
        } else if (priority != null) {
            tasks = getUserTasksUseCase.executeByPriority(userId, priority);
        } else {
            tasks = getUserTasksUseCase.execute(userId);
        }
        
        List<TaskResponse> response = taskMapper.toResponseList(tasks);
        
        log.info("Retrieved {} tasks for user {}", tasks.size(), userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Busca uma tarefa específica por ID
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "Buscar tarefa por ID", description = "Busca uma tarefa específica pelo ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarefa encontrada",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long taskId, HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        
        // Busca todas as tarefas do usuário e filtra pela ID (para verificar ownership)
        List<Task> userTasks = getUserTasksUseCase.execute(userId);
        Task task = userTasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst()
                .orElseThrow(() -> new TaskNotFoundException(taskId));
        
        TaskResponse response = taskMapper.toResponse(task);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza uma tarefa existente
     */
    @PutMapping("/{taskId}")
    @Operation(summary = "Atualizar tarefa", description = "Atualiza uma tarefa existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long taskId, 
                                                   @Valid @RequestBody TaskRequest taskRequest,
                                                   HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        
        log.info("Updating task {} for user {}", taskId, userId);
        
        Result<Task, Exception> result = updateTaskUseCase.execute(taskId, taskRequest, userId);
        
        if (result.isSuccess()) {
            Task task = result.getValueOrThrow();
            TaskResponse response = taskMapper.toResponse(task);
            
            log.info("Task {} updated successfully", taskId);
            return ResponseEntity.ok(response);
        } else {
            // O GlobalExceptionHandler irá tratar a exceção automaticamente
            Exception error = result.getError().get();
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            } else {
                throw new RuntimeException(error);
            }
        }
    }

    /**
     * Atualiza o status de uma tarefa
     */
    @PatchMapping("/{taskId}/status")
    @Operation(summary = "Atualizar status da tarefa", description = "Atualiza apenas o status de uma tarefa")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Status inválido"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Long taskId,
                                                         @Parameter(description = "Novo status") @RequestParam TaskStatus status,
                                                         HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        
        log.info("Updating status of task {} to {} for user {}", taskId, status, userId);
        
        Task task = updateTaskStatusUseCase.execute(taskId, status, userId);
        TaskResponse response = taskMapper.toResponse(task);
        
        log.info("Task {} status updated to {} successfully", taskId, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Exclui uma tarefa
     */
    @DeleteMapping("/{taskId}")
    @Operation(summary = "Excluir tarefa", description = "Exclui uma tarefa do usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tarefa excluída com sucesso"),
        @ApiResponse(responseCode = "404", description = "Tarefa não encontrada"),
        @ApiResponse(responseCode = "401", description = "Não autorizado")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId, HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        
        log.info("Deleting task {} for user {}", taskId, userId);
        
        deleteTaskUseCase.execute(taskId, userId);
        
        log.info("Task {} deleted successfully", taskId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista tarefas atrasadas do usuário
     */
    @GetMapping("/overdue")
    @Operation(summary = "Listar tarefas atrasadas", description = "Lista todas as tarefas atrasadas do usuário")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(HttpServletRequest request) {
        Long userId = extractUserIdFromToken(request);
        
        List<Task> overdueTasks = getUserTasksUseCase.executeOverdueTasks(userId);
        List<TaskResponse> response = taskMapper.toResponseList(overdueTasks);
        
        log.info("Retrieved {} overdue tasks for user {}", overdueTasks.size(), userId);
        return ResponseEntity.ok(response);
    }
}