package com.tiagoreiz.projeto.Infra.Persistence.Mappers;

import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Infra.Persistence.Entities.TaskPersistence;
import com.tiagoreiz.projeto.Infra.Persistence.Entities.UserPersistence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper MapStruct para conversão entre entidades Task do domínio e TaskPersistence da infraestrutura.
 * Responsável por isolar a camada de domínio das especificidades da persistência.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface TaskPersistenceMapper {
    
    TaskPersistenceMapper INSTANCE = Mappers.getMapper(TaskPersistenceMapper.class);
    
    /**
     * Converte uma entidade Task do domínio para TaskPersistence da infraestrutura
     * 
     * @param task Entidade do domínio
     * @return Entidade de persistência
     */
    @Mapping(target = "user", ignore = true) // O usuário será definido separadamente
    TaskPersistence toEntity(Task task);
    
    /**
     * Converte uma entidade Task do domínio para TaskPersistence com o usuário especificado
     * 
     * @param task Entidade do domínio
     * @param user Usuário de persistência
     * @return Entidade de persistência
     */
    @Mapping(target = "user", source = "user")
    @Mapping(target = "id", source = "task.id")
    @Mapping(target = "title", source = "task.title")
    @Mapping(target = "description", source = "task.description")
    @Mapping(target = "status", source = "task.status")
    @Mapping(target = "priority", source = "task.priority")
    @Mapping(target = "createdAt", source = "task.createdAt")
    @Mapping(target = "updatedAt", source = "task.updatedAt")
    @Mapping(target = "dueDate", source = "task.dueDate")
    @Mapping(target = "completedAt", source = "task.completedAt")
    TaskPersistence toEntityWithUser(Task task, UserPersistence user);
    
    /**
     * Converte uma entidade TaskPersistence da infraestrutura para Task do domínio
     * 
     * @param taskPersistence Entidade de persistência
     * @return Entidade do domínio
     */
    @Mapping(target = "userId", source = "user.id")
    Task toDomain(TaskPersistence taskPersistence);
    
    /**
     * Converte uma lista de entidades Task do domínio para TaskPersistence da infraestrutura
     * 
     * @param tasks Lista de entidades do domínio
     * @return Lista de entidades de persistência
     */
    List<TaskPersistence> toEntityList(List<Task> tasks);
    
    /**
     * Converte uma lista de entidades TaskPersistence da infraestrutura para Task do domínio
     * 
     * @param taskPersistenceList Lista de entidades de persistência
     * @return Lista de entidades do domínio
     */
    List<Task> toDomainList(List<TaskPersistence> taskPersistenceList);
}