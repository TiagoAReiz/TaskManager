package com.tiagoreiz.projeto.Adapters.Controllers;

import com.tiagoreiz.projeto.Adapters.DTOs.LoginRequest;
import com.tiagoreiz.projeto.Adapters.DTOs.LoginResponse;
import com.tiagoreiz.projeto.Adapters.DTOs.UserRegistrationRequest;
import com.tiagoreiz.projeto.Adapters.Mappers.UserMapper;
import com.tiagoreiz.projeto.Application.UseCases.User.LoginUserUseCase;
import com.tiagoreiz.projeto.Application.UseCases.User.RegisterUserUseCase;
import com.tiagoreiz.projeto.Core.Common.Result;
import com.tiagoreiz.projeto.Core.Entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável pelos endpoints de autenticação e registro de usuários.
 * Expõe APIs para registro de novos usuários e login.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Endpoints para autenticação e registro de usuários")
public class AuthController {
    
    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final UserMapper userMapper;
    
    /**
     * Registra um novo usuário no sistema
     * 
     * @param request Dados do usuário a ser registrado
     * @return Resposta com dados do usuário registrado e token JWT
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já existe",
                content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content)
    })
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());
        
        // Registra o usuário
        Result<User, Exception> registerResult = registerUserUseCase.execute(request);
        
        if (registerResult.isFailure()) {
            // O GlobalExceptionHandler irá tratar a exceção automaticamente
            Exception error = registerResult.getError().get();
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            } else {
                throw new RuntimeException(error);
            }
        }
        
        User user = registerResult.getValueOrThrow();
        
        // Faz login automático após o registro
        LoginRequest loginRequest = new LoginRequest(request.getEmail(), request.getPassword());
        Result<LoginUserUseCase.LoginResult, Exception> loginResult = loginUserUseCase.execute(loginRequest);
        
        if (loginResult.isFailure()) {
            // O GlobalExceptionHandler irá tratar a exceção automaticamente
            Exception error = loginResult.getError().get();
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            } else {
                throw new RuntimeException(error);
            }
        }
        
        // Converte para DTO de resposta
        LoginResponse response = userMapper.toLoginResponse(loginResult.getValueOrThrow().getUser(), loginResult.getValueOrThrow().getToken());
        
        log.info("User registered and logged in successfully: {}", user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Autentica um usuário existente
     * 
     * @param request Credenciais de login
     * @return Resposta com token JWT e dados do usuário
     */
    @PostMapping("/login")
    @Operation(summary = "Fazer login", description = "Autentica um usuário existente e retorna um token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
                content = @Content),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                content = @Content),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content)
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        // Autentica o usuário
        Result<LoginUserUseCase.LoginResult, Exception> loginResult = loginUserUseCase.execute(request);
        
        if (loginResult.isFailure()) {
            // O GlobalExceptionHandler irá tratar a exceção automaticamente
            Exception error = loginResult.getError().get();
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            } else {
                throw new RuntimeException(error);
            }
        }
        
        // Converte para DTO de resposta
        LoginResponse response = userMapper.toLoginResponse(loginResult.getValueOrThrow().getUser(), loginResult.getValueOrThrow().getToken());
        
        log.info("Login successful for user: {}", request.getEmail());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint para verificar se a API está funcionando
     * 
     * @return Mensagem de status
     */
    @GetMapping("/status")
    @Operation(summary = "Verificar status da API", description = "Endpoint público para verificar se a API está funcionando")
    @ApiResponse(responseCode = "200", description = "API funcionando corretamente")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("TaskMaster API is running");
    }
    
}