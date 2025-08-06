package com.tiagoreiz.projeto.Infra.Persistence.Repositories;

import com.tiagoreiz.projeto.Core.Entities.User;
import com.tiagoreiz.projeto.Core.Repositories.UserRepository;
import com.tiagoreiz.projeto.Infra.Persistence.Entities.UserPersistence;
import com.tiagoreiz.projeto.Infra.Persistence.Mappers.UserPersistenceMapper;
import com.tiagoreiz.projeto.Infra.Persistence.SpringData.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementação do repositório de usuários usando Spring Data JPA.
 * Adapta as operações do repositório do domínio para a infraestrutura de persistência.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository userJpaRepository;
    private final UserPersistenceMapper userMapper;
    
    @Override
    public User save(User user) {
        UserPersistence userPersistence = userMapper.toEntity(user);
        UserPersistence savedUser = userJpaRepository.save(userPersistence);
        return userMapper.toDomain(savedUser);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id)
                .map(userMapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }
    
    @Override
    public List<User> findAll() {
        List<UserPersistence> userPersistenceList = userJpaRepository.findAll();
        return userMapper.toDomainList(userPersistenceList);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
    
    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }
    
    @Override
    public User update(User user) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null for update operation");
        }
        
        Optional<UserPersistence> existingUser = userJpaRepository.findById(user.getId());
        if (existingUser.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + user.getId());
        }
        
        UserPersistence userPersistence = userMapper.toEntity(user);
        UserPersistence updatedUser = userJpaRepository.save(userPersistence);
        return userMapper.toDomain(updatedUser);
    }
}