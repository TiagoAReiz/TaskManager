package com.tiagoreiz.projeto.Application.UseCases.User;

import com.tiagoreiz.projeto.Adapters.DTOs.LoginRequest;
import com.tiagoreiz.projeto.Core.Common.Result;
import com.tiagoreiz.projeto.Core.Entities.User;
import com.tiagoreiz.projeto.Core.Exceptions.InvalidCredentialsException;
import com.tiagoreiz.projeto.Core.Exceptions.TaskValidationException;
import com.tiagoreiz.projeto.Core.Repositories.UserRepository;
import com.tiagoreiz.projeto.Infra.Security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Caso de uso para autenticação de usuários no sistema.
 * Responsável pela lógica de negócio do login e geração de tokens JWT.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class LoginUserUseCase {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    /**
     * Resultado do processo de login
     */
    public static class LoginResult {
        private final String token;
        private final User user;
        
        public LoginResult(String token, User user) {
            this.token = token;
            this.user = user;
        }
        
        public String getToken() {
            return token;
        }
        
        public User getUser() {
            return user;
        }
    }
    
    /**
     * Autentica um usuário usando DTO e gera um token JWT
     * 
     * @param loginRequest DTO com credenciais de login
     * @return Result contendo LoginResult ou erro
     */
    public Result<LoginResult, Exception> execute(LoginRequest loginRequest) {
        
        try {
            // Validações de entrada
            Result<Void, TaskValidationException> validationResult = validateLoginRequest(loginRequest);
            if (validationResult.isFailure()) {
                return Result.failure(validationResult.getError().get());
            }
            
            // Autentica o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            
            // Carrega os detalhes do usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
            
            // Busca o usuário no repositório
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("User not found"));
            
            // Gera o token JWT
            String token = jwtService.generateToken(userDetails, user.getId());
            
            return Result.success(new LoginResult(token, user));
            
        } catch (AuthenticationException e) {
            return Result.failure(new InvalidCredentialsException());
        } catch (Exception e) {
            return Result.failure(e);
        }
    }

    /**
     * Autentica um usuário e gera um token JWT (método legado)
     * 
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return LoginResult contendo o token e dados do usuário
     * @throws AuthenticationException Se as credenciais são inválidas
     * @throws IllegalArgumentException Se os dados de entrada são inválidos
     */
    public LoginResult execute(String email, String password) {
        // Validações de entrada
        validateInput(email, password);
        
        try {
            // Autentica o usuário
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            
            // Carrega os detalhes do usuário
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            
            // Busca o usuário no repositório
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            // Gera o token JWT com o ID do usuário
            String token = jwtService.generateTokenWithUserId(userDetails, user.getId());
            
            return new LoginResult(token, user);
            
        } catch (AuthenticationException e) {
            throw new AuthenticationException("Invalid email or password") {};
        }
    }
    
    /**
     * Valida os dados de entrada para login
     * 
     * @param email Email do usuário
     * @param password Senha do usuário
     * @throws IllegalArgumentException Se algum dado é inválido
     */
    private void validateInput(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
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
     * Valida os dados de entrada do DTO de login
     * 
     * @param loginRequest DTO a ser validado
     * @return Result indicando sucesso ou erro de validação
     */
    private Result<Void, TaskValidationException> validateLoginRequest(LoginRequest loginRequest) {
        if (loginRequest == null) {
            return Result.failure(new TaskValidationException("Login request cannot be null"));
        }
        
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            return Result.failure(new TaskValidationException("Email cannot be null or empty"));
        }
        
        if (!isValidEmail(loginRequest.getEmail())) {
            return Result.failure(new TaskValidationException("Email format is invalid"));
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return Result.failure(new TaskValidationException("Password cannot be null or empty"));
        }
        
        return Result.success(null);
    }
}