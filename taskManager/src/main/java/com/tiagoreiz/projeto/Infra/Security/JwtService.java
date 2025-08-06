package com.tiagoreiz.projeto.Infra.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Serviço responsável pela geração, validação e manipulação de tokens JWT.
 * Utiliza a biblioteca JJWT para operações com JSON Web Tokens.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Service
public class JwtService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    /**
     * Extrai o username (email) do token JWT
     * 
     * @param token Token JWT
     * @return Username extraído do token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * Extrai uma claim específica do token JWT
     * 
     * @param token Token JWT
     * @param claimsResolver Função para extrair a claim
     * @param <T> Tipo da claim
     * @return Valor da claim extraída
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * Gera um token JWT para o usuário
     * 
     * @param userDetails Detalhes do usuário
     * @return Token JWT gerado
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    
    /**
     * Gera um token JWT com claims adicionais
     * 
     * @param extraClaims Claims adicionais
     * @param userDetails Detalhes do usuário
     * @return Token JWT gerado
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(jwtExpiration, ChronoUnit.MILLIS)))
                .signWith(getSignInKey())
                .compact();
    }
    
    /**
     * Verifica se o token JWT é válido para o usuário
     * 
     * @param token Token JWT
     * @param userDetails Detalhes do usuário
     * @return true se o token é válido
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    
    /**
     * Verifica se o token JWT está expirado
     * 
     * @param token Token JWT
     * @return true se o token está expirado
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * Extrai a data de expiração do token JWT
     * 
     * @param token Token JWT
     * @return Data de expiração
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Extrai todas as claims do token JWT
     * 
     * @param token Token JWT
     * @return Claims do token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * Obtém a chave de assinatura para o JWT
     * 
     * @return Chave secreta para assinatura
     */
    private SecretKey getSignInKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Extrai o ID do usuário do token JWT (se disponível)
     * 
     * @param token Token JWT
     * @return ID do usuário ou null se não disponível
     */
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> {
            Object userId = claims.get("userId");
            return userId != null ? Long.valueOf(userId.toString()) : null;
        });
    }
    
    /**
     * Gera um token JWT com o ID do usuário incluído
     * 
     * @param userDetails Detalhes do usuário
     * @param userId ID do usuário
     * @return Token JWT gerado
     */
    public String generateTokenWithUserId(UserDetails userDetails, Long userId) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", userId);
        return generateToken(extraClaims, userDetails);
    }
}