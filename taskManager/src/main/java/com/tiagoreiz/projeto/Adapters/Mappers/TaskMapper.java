package com.tiagoreiz.projeto.Adapters.Mappers;

import com.tiagoreiz.projeto.Adapters.DTOs.TaskRequest;
import com.tiagoreiz.projeto.Adapters.DTOs.TaskResponse;
import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mapper MapStruct para conversão entre DTOs e entidades Task.
 * Responsável por converter dados de entrada/saída da API para entidades de domínio.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface TaskMapper {
    
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);
    
    /**
     * Converte um TaskRequest para entidade Task do domínio
     * 
     * @param request DTO de requisição de tarefa
     * @param userId ID do usuário proprietário
     * @return Entidade Task do domínio
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true) // Será definido como PENDING no construtor
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "userId", source = "userId")
    Task toEntity(TaskRequest request, Long userId);
    
    /**
     * Converte uma entidade Task para TaskResponse
     * 
     * @param task Entidade Task do domínio
     * @return DTO de resposta de tarefa
     */
    @Mapping(target = "isOverdue", ignore = true) // Será calculado no TaskResponse
    @Mapping(target = "daysUntilDue", ignore = true) // Será calculado no TaskResponse
    TaskResponse toResponse(Task task);
    
    /**
     * Converte uma lista de entidades Task para lista de TaskResponse
     * 
     * @param tasks Lista de entidades Task do domínio
     * @return Lista de DTOs de resposta de tarefa
     */
    List<TaskResponse> toResponseList(List<Task> tasks);
    
    /**
     * Cria uma entidade Task a partir de dados básicos
     * Usado principalmente para testes ou criação manual
     * 
     * @param title Título da tarefa
     * @param description Descrição da tarefa
     * @param priority Prioridade da tarefa
     * @param dueDate Data limite
     * @param userId ID do usuário proprietário
     * @return Entidade Task do domínio
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true) // Será definido como PENDING no construtor
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    Task createTask(String title, String description, TaskPriority priority, 
                   LocalDateTime dueDate, Long userId);
    
    /**
     * Atualiza uma entidade Task existente com dados de um TaskRequest
     * Mantém os campos que não devem ser alterados
     * 
     * @param existingTask Tarefa existente
     * @param request Dados de atualização
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    void updateTaskFromRequest(TaskRequest request, @org.mapstruct.MappingTarget Task existingTask);
}