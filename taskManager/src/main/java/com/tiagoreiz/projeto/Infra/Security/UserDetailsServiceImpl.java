package com.tiagoreiz.projeto.Infra.Security;

import com.tiagoreiz.projeto.Infra.Persistence.Entities.UserPersistence;
import com.tiagoreiz.projeto.Infra.Persistence.SpringData.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementação do UserDetailsService para autenticação com Spring Security.
 * Responsável por carregar os detalhes do usuário durante o processo de autenticação.
 * 
 * @author Tiago Reiz
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserJpaRepository userJpaRepository;
    
    /**
     * Carrega um usuário pelo email (username) para autenticação
     * 
     * @param email Email do usuário
     * @return UserDetails contendo as informações do usuário
     * @throws UsernameNotFoundException Se o usuário não for encontrado
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserPersistence user = userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
    
    /**
     * Busca um usuário persistence pelo email
     * Método auxiliar para outros serviços que precisam do objeto completo
     * 
     * @param email Email do usuário
     * @return UserPersistence do usuário
     * @throws UsernameNotFoundException Se o usuário não for encontrado
     */
    public UserPersistence getUserPersistenceByEmail(String email) throws UsernameNotFoundException {
        return userJpaRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}