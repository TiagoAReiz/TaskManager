package com.tiagoreiz.projeto.Adapters.Mappers;

import com.tiagoreiz.projeto.Adapters.DTOs.LoginResponse;
import com.tiagoreiz.projeto.Adapters.DTOs.UserRegistrationRequest;
import com.tiagoreiz.projeto.Core.Entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper MapStruct para conversão entre DTOs e entidades User.
 * Responsável por converter dados de entrada/saída da API para entidades de domínio.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    /**
     * Converte um UserRegistrationRequest para entidade User do domínio
     * 
     * @param request DTO de requisição de registro
     * @return Entidade User do domínio
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    User toEntity(UserRegistrationRequest request);
    
    /**
     * Converte uma entidade User para LoginResponse
     * 
     * @param user Entidade User do domínio
     * @param token Token JWT gerado
     * @return DTO de resposta de login
     */
    @Mapping(target = "token", source = "token")
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "userCreatedAt", source = "user.createdAt")
    LoginResponse toLoginResponse(User user, String token);
    
    /**
     * Cria um User básico a partir de dados simples
     * Usado principalmente para testes ou criação manual
     * 
     * @param name Nome do usuário
     * @param email Email do usuário
     * @param encodedPassword Senha já criptografada
     * @return Entidade User do domínio
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "password", source = "encodedPassword")
    User createUser(String name, String email, String encodedPassword);
}