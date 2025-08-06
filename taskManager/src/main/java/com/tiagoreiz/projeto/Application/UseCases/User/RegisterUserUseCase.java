package com.tiagoreiz.projeto.Application.UseCases.User;

import com.tiagoreiz.projeto.Adapters.DTOs.UserRegistrationRequest;
import com.tiagoreiz.projeto.Core.Common.Result;
import com.tiagoreiz.projeto.Core.Entities.User;
import com.tiagoreiz.projeto.Core.Exceptions.TaskValidationException;
import com.tiagoreiz.projeto.Core.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para registro de novos usuários no sistema.
 * Responsável pela lógica de negócio do cadastro de usuários.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * Registra um novo usuário no sistema usando DTO
     * 
     * @param registrationRequest DTO com dados de registro
     * @return Result contendo o usuário registrado ou erro
     */
    public Result<User, Exception> execute(UserRegistrationRequest registrationRequest) {
        
        try {
            // Validações de entrada
            Result<Void, TaskValidationException> validationResult = validateRegistrationRequest(registrationRequest);
            if (validationResult.isFailure()) {
                return Result.failure(validationResult.getError().get());
            }
            
            // Verifica se o email já existe
            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
                return Result.failure(new IllegalArgumentException("Email already exists: " + registrationRequest.getEmail()));
            }
            
            // Criptografa a senha
            String encodedPassword = passwordEncoder.encode(registrationRequest.getPassword());
            
            // Cria a entidade User
            User user = mapToUser(registrationRequest, encodedPassword);
            
            // Salva o usuário
            User savedUser = userRepository.save(user);
            return Result.success(savedUser);
            
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    /**
     * Registra um novo usuário no sistema (método legado)
     * 
     * @param name Nome do usuário
     * @param email Email do usuário
     * @param password Senha em texto plano
     * @return Usuário registrado
     * @throws IllegalArgumentException Se os dados são inválidos ou email já existe
     */
    public User execute(String name, String email, String password) {
        // Validações de entrada
        validateInput(name, email, password);
        
        // Verifica se o email já existe
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        
        // Criptografa a senha
        String encodedPassword = passwordEncoder.encode(password);
        
        // Cria a entidade User
        User user = new User(name, email, encodedPassword);
        
        // Salva o usuário
        return userRepository.save(user);
    }
    
    /**
     * Valida os dados de entrada para registro
     * 
     * @param name Nome do usuário
     * @param email Email do usuário
     * @param password Senha do usuário
     * @throws IllegalArgumentException Se algum dado é inválido
     */
    private void validateInput(String name, String email, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        if (name.length() < 2 || name.length() > 100) {
            throw new IllegalArgumentException("Name must be between 2 and 100 characters");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (email.length() > 150) {
            throw new IllegalArgumentException("Email must not exceed 150 characters");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        
        if (password.length() > 100) {
            throw new IllegalArgumentException("Password must not exceed 100 characters");
        }
    }
    
    /**
     * Valida o formato do email usando regex simples
     * 
     * @param email Email a ser validado
     * @return true se o email tem formato válido
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);
    }
    
    /**
     * Valida os dados de entrada do DTO de registro
     * 
     * @param registrationRequest DTO a ser validado
     * @return Result indicando sucesso ou erro de validação
     */
    private Result<Void, TaskValidationException> validateRegistrationRequest(UserRegistrationRequest registrationRequest) {
        if (registrationRequest == null) {
            return Result.failure(new TaskValidationException("Registration request cannot be null"));
        }
        
        if (registrationRequest.getName() == null || registrationRequest.getName().trim().isEmpty()) {
            return Result.failure(new TaskValidationException("Name cannot be null or empty"));
        }
        
        if (registrationRequest.getName().length() < 2 || registrationRequest.getName().length() > 100) {
            return Result.failure(new TaskValidationException("Name must be between 2 and 100 characters"));
        }
        
        if (registrationRequest.getEmail() == null || registrationRequest.getEmail().trim().isEmpty()) {
            return Result.failure(new TaskValidationException("Email cannot be null or empty"));
        }
        
        if (registrationRequest.getEmail().length() > 150) {
            return Result.failure(new TaskValidationException("Email must not exceed 150 characters"));
        }
        
        if (!isValidEmail(registrationRequest.getEmail())) {
            return Result.failure(new TaskValidationException("Email format is invalid"));
        }
        
        if (registrationRequest.getPassword() == null || registrationRequest.getPassword().trim().isEmpty()) {
            return Result.failure(new TaskValidationException("Password cannot be null or empty"));
        }
        
        if (registrationRequest.getPassword().length() < 6 || registrationRequest.getPassword().length() > 100) {
            return Result.failure(new TaskValidationException("Password must be between 6 and 100 characters"));
        }
        
        return Result.success(null);
    }
    
    /**
     * Mapeia um UserRegistrationRequest DTO para uma entidade User
     * 
     * @param registrationRequest DTO com dados de registro
     * @param encodedPassword Senha já criptografada
     * @return Entidade User
     */
    private User mapToUser(UserRegistrationRequest registrationRequest, String encodedPassword) {
        return new User(
            registrationRequest.getName(),
            registrationRequest.getEmail(),
            encodedPassword
        );
    }
}