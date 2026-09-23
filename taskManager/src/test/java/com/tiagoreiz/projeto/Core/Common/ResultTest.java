package com.tiagoreiz.projeto.Core.Common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResultTest {

    @Test
    void successExposesValueAndMaps() {
        Result<Integer, String> result = Result.success(21);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getError()).isEmpty();
        assertThat(result.map(v -> v * 2).getValueOrThrow()).isEqualTo(42);
    }

    @Test
    void failureExposesErrorAndDoesNotMapValue() {
        Result<Integer, String> result = Result.failure("boom");

        assertThat(result.isFailure()).isTrue();
        assertThat(result.getValue()).isEmpty();
        assertThat(result.getError()).contains("boom");
        assertThat(result.map(v -> v * 2).getError()).contains("boom");
        assertThat(result.mapError(String::length).getError()).contains(4);
        assertThatThrownBy(result::getValueOrThrow).isInstanceOf(RuntimeException.class);
    }
}
