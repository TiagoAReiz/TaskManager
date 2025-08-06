package com.tiagoreiz.projeto.Infra.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticação JWT que intercepta todas as requisições HTTP.
 * Responsável por extrair e validar tokens JWT do cabeçalho Authorization.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    /**
     * Filtra cada requisição HTTP para verificar e processar tokens JWT
     * 
     * @param request Requisição HTTP
     * @param response Resposta HTTP
     * @param filterChain Cadeia de filtros
     * @throws ServletException Se ocorrer erro na servlet
     * @throws IOException Se ocorrer erro de I/O
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        // Verifica se o header Authorization está presente e contém Bearer token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extrai o token JWT do header
        jwt = authHeader.substring(7);
        
        try {
            // Extrai o email do usuário do token
            userEmail = jwtService.extractUsername(jwt);
            
            // Se o email foi extraído e o usuário não está autenticado no contexto
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Carrega os detalhes do usuário
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                // Valida o token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    
                    // Cria o token de autenticação
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Define detalhes adicionais da autenticação
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Define a autenticação no contexto de segurança
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("User '{}' authenticated successfully with JWT token", userEmail);
                } else {
                    log.warn("Invalid JWT token for user '{}'", userEmail);
                }
            }
        } catch (Exception e) {
            log.error("Error processing JWT token: {}", e.getMessage());
            // Continue com a cadeia de filtros mesmo em caso de erro
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Determina se este filtro deve ser aplicado à requisição
     * 
     * @param request Requisição HTTP
     * @return true se o filtro deve ser aplicado
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        // Não aplica o filtro para endpoints públicos
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/api-docs/") ||
               path.startsWith("/actuator/") ||
               path.equals("/") ||
               path.equals("/favicon.ico");
    }
}