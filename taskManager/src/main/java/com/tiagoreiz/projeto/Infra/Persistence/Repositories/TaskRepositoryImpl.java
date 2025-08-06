package com.tiagoreiz.projeto.Infra.Persistence.Repositories;

import com.tiagoreiz.projeto.Core.Entities.Task;
import com.tiagoreiz.projeto.Core.Entities.TaskPriority;
import com.tiagoreiz.projeto.Core.Entities.TaskStatus;
import com.tiagoreiz.projeto.Core.Exceptions.ResourceNotFoundException;
import com.tiagoreiz.projeto.Core.Repositories.TaskRepository;
import com.tiagoreiz.projeto.Infra.Persistence.Entities.TaskPersistence;
import com.tiagoreiz.projeto.Infra.Persistence.Entities.UserPersistence;
import com.tiagoreiz.projeto.Infra.Persistence.Mappers.TaskPersistenceMapper;
import com.tiagoreiz.projeto.Infra.Persistence.SpringData.TaskJpaRepository;
import com.tiagoreiz.projeto.Infra.Persistence.SpringData.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de tarefas usando Spring Data JPA.
 * Adapta as operações do repositório do domínio para a infraestrutura de persistência.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {
    
    private final TaskJpaRepository taskJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final TaskPersistenceMapper taskMapper;
    
    @Override
    public Task save(Task task) {
        UserPersistence user = userJpaRepository.findById(task.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", task.getUserId()));
        
        TaskPersistence taskPersistence = taskMapper.toEntityWithUser(task, user);
        TaskPersistence savedTask = taskJpaRepository.save(taskPersistence);
        return taskMapper.toDomain(savedTask);
    }
    
    @Override
    public Optional<Task> findById(Long id) {
        return taskJpaRepository.findById(id)
                .map(taskMapper::toDomain);
    }
    
    @Override
    public List<Task> findByUserId(Long userId) {
        List<TaskPersistence> taskPersistenceList = taskJpaRepository.findByUserId(userId);
        return taskMapper.toDomainList(taskPersistenceList);
    }
    
    @Override
    public List<Task> findByUserIdAndStatus(Long userId, TaskStatus status) {
        List<TaskPersistence> taskPersistenceList = taskJpaRepository.findByUserIdAndStatus(userId, status);
        return taskMapper.toDomainList(taskPersistenceList);
    }
    
    @Override
    public List<Task> findByUserIdAndPriority(Long userId, TaskPriority priority) {
        List<TaskPersistence> taskPersistenceList = taskJpaRepository.findByUserIdAndPriority(userId, priority);
        return taskMapper.toDomainList(taskPersistenceList);
    }
    
    @Override
    public List<Task> findByUserIdAndStatusAndPriority(Long userId, TaskStatus status, TaskPriority priority) {
        List<TaskPersistence> taskPersistenceList = taskJpaRepository.findByUserIdAndStatusAndPriority(userId, status, priority);
        return taskMapper.toDomainList(taskPersistenceList);
    }
    
    @Override
    public List<Task> findByUserIdAndDueDateBefore(Long userId, LocalDateTime dueDate) {
        List<TaskPersistence> taskPersistenceList = taskJpaRepository.findByUserIdAndDueDateBefore(userId, dueDate);
        return taskMapper.toDomainList(taskPersistenceList);
    }
    
    @Override
    public List<Task> findOverdueTasksByUserId(Long userId) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<TaskPersistence> taskPersistenceList = taskJpaRepository.findOverdueTasksByUserId(userId, currentDateTime);
        return taskMapper.toDomainList(taskPersistenceList);
    }
    
    @Override
    public long countByUserIdAndStatus(Long userId, TaskStatus status) {
        return taskJpaRepository.countByUserIdAndStatus(userId, status);
    }
    
    @Override
    public void deleteById(Long id) {
        taskJpaRepository.deleteById(id);
    }
    
    @Override
    public void deleteByUserId(Long userId) {
        taskJpaRepository.deleteByUserId(userId);
    }
    
    @Override
    public Task update(Task task) {
        if (task.getId() == null) {
            throw new IllegalArgumentException("Task ID cannot be null for update operation");
        }
        
        Optional<TaskPersistence> existingTask = taskJpaRepository.findById(task.getId());
        if (existingTask.isEmpty()) {
            throw new ResourceNotFoundException("Task", task.getId());
        }
        
        UserPersistence user = userJpaRepository.findById(task.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", task.getUserId()));
        
        TaskPersistence taskPersistence = taskMapper.toEntityWithUser(task, user);
        TaskPersistence updatedTask = taskJpaRepository.save(taskPersistence);
        return taskMapper.toDomain(updatedTask);
    }
    
    @Override
    public List<Task> findAll() {
        List<TaskPersistence> taskPersistenceList = taskJpaRepository.findAll();
        return taskMapper.toDomainList(taskPersistenceList);
    }
}