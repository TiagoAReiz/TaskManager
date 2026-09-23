package com.tiagoreiz.projeto.Application.UseCases.User;

import com.tiagoreiz.projeto.Adapters.DTOs.UserRegistrationRequest;
import com.tiagoreiz.projeto.Core.Common.Result;
import com.tiagoreiz.projeto.Core.Entities.User;
import com.tiagoreiz.projeto.Core.Exceptions.TaskValidationException;
import com.tiagoreiz.projeto.Core.Repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase useCase;

    @Test
    void storesUserWithEncodedPassword() {
        UserRegistrationRequest request = new UserRegistrationRequest("Ana Souza", "ana@mail.com", "segredo123");
        when(userRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("segredo123")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Result<User, Exception> result = useCase.execute(request);

        assertThat(result.isSuccess()).isTrue();
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).isEqualTo("bcrypt-hash");
        assertThat(saved.getValue().getEmail()).isEqualTo("ana@mail.com");
    }

    @Test
    void rejectsDuplicateEmail() {
        UserRegistrationRequest request = new UserRegistrationRequest("Ana Souza", "ana@mail.com", "segredo123");
        when(userRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        Result<User, Exception> result = useCase.execute(request);

        assertThat(result.getError().get())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void rejectsInvalidEmailAndShortPassword() {
        Result<User, Exception> badEmail = useCase.execute(new UserRegistrationRequest("Ana", "not-an-email", "segredo123"));
        Result<User, Exception> shortPassword = useCase.execute(new UserRegistrationRequest("Ana", "ana@mail.com", "123"));

        assertThat(badEmail.getError().get()).isInstanceOf(TaskValidationException.class);
        assertThat(shortPassword.getError().get()).isInstanceOf(TaskValidationException.class);
        verify(userRepository, never()).save(any());
    }
}
