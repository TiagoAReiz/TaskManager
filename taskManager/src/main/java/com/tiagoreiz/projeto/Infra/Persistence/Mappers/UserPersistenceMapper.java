package com.tiagoreiz.projeto.Infra.Persistence.Mappers;

import com.tiagoreiz.projeto.Core.Entities.User;
import com.tiagoreiz.projeto.Infra.Persistence.Entities.UserPersistence;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper MapStruct para conversão entre entidades User do domínio e UserPersistence da infraestrutura.
 * Responsável por isolar a camada de domínio das especificidades da persistência.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Mapper(componentModel = "spring", uses = {TaskPersistenceMapper.class})
public interface UserPersistenceMapper {
    
    UserPersistenceMapper INSTANCE = Mappers.getMapper(UserPersistenceMapper.class);
    
    /**
     * Converte uma entidade User do domínio para UserPersistence da infraestrutura
     * 
     * @param user Entidade do domínio
     * @return Entidade de persistência
     */
    @Mapping(target = "tasks", ignore = true) // Evita referência circular
    UserPersistence toEntity(User user);
    
    /**
     * Converte uma entidade UserPersistence da infraestrutura para User do domínio
     * 
     * @param userPersistence Entidade de persistência
     * @return Entidade do domínio
     */
    @Mapping(target = "tasks", ignore = true) // Evita referência circular, será mapeado separadamente se necessário
    User toDomain(UserPersistence userPersistence);
    
    /**
     * Converte uma lista de entidades User do domínio para UserPersistence da infraestrutura
     * 
     * @param users Lista de entidades do domínio
     * @return Lista de entidades de persistência
     */
    List<UserPersistence> toEntityList(List<User> users);
    
    /**
     * Converte uma lista de entidades UserPersistence da infraestrutura para User do domínio
     * 
     * @param userPersistenceList Lista de entidades de persistência
     * @return Lista de entidades do domínio
     */
    List<User> toDomainList(List<UserPersistence> userPersistenceList);
}