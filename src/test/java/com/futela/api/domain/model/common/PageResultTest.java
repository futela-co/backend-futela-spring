package com.futela.api.domain.model.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResultTest {

    @Test
    @DisplayName("Doit calculer le nombre total de pages correctement")
    void shouldCalculateTotalPages() {
        PageResult<String> result = new PageResult<>(List.of("a", "b"), 0, 10, 25);

        assertThat(result.totalPages()).isEqualTo(3); // ceil(25/10) = 3
    }

    @Test
    @DisplayName("Doit retourner 1 page quand le total est inférieur à la taille")
    void shouldReturnOnePageWhenTotalLessThanSize() {
        PageResult<String> result = new PageResult<>(List.of("a"), 0, 20, 5);

        assertThat(result.totalPages()).isEqualTo(1);
    }

    @Test
    @DisplayName("Doit retourner 0 pages quand la taille est 0")
    void shouldReturnZeroPagesWhenSizeIsZero() {
        PageResult<String> result = new PageResult<>(List.of(), 0, 0, 10);

        assertThat(result.totalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("Doit retourner 0 pages quand il n'y a aucun élément")
    void shouldReturnZeroPagesWhenNoElements() {
        PageResult<String> result = new PageResult<>(List.of(), 0, 10, 0);

        assertThat(result.totalPages()).isEqualTo(0);
    }

    @Test
    @DisplayName("Doit calculer correctement quand totalElements est un multiple de size")
    void shouldCalculateExactPages() {
        PageResult<String> result = new PageResult<>(List.of("a"), 0, 10, 30);

        assertThat(result.totalPages()).isEqualTo(3); // 30/10 = 3 exact
    }
}
