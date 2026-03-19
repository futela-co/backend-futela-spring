package com.futela.api.domain.model.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PageableTest {

    @Test
    @DisplayName("Doit utiliser les valeurs par défaut quand les paramètres sont null ou invalides")
    void shouldUseDefaultValues() {
        Pageable pageable = new Pageable(0, 0, null, null);

        assertThat(pageable.page()).isEqualTo(0);
        assertThat(pageable.size()).isEqualTo(20);
        assertThat(pageable.sortBy()).isEqualTo("createdAt");
        assertThat(pageable.sortDirection()).isEqualTo("DESC");
    }

    @Test
    @DisplayName("Doit corriger une page négative à 0")
    void shouldSetNegativePageToZero() {
        Pageable pageable = new Pageable(-5, 10, "name", "ASC");

        assertThat(pageable.page()).isEqualTo(0);
    }

    @Test
    @DisplayName("Doit limiter la taille à 500 si elle dépasse")
    void shouldCapSizeTo500() {
        Pageable pageable = new Pageable(0, 1000, "name", "ASC");

        assertThat(pageable.size()).isEqualTo(500);
    }

    @Test
    @DisplayName("Doit mettre la taille à 20 si elle est inférieure ou égale à 0")
    void shouldSetSizeTo20WhenZeroOrNegative() {
        Pageable pageableZero = new Pageable(0, 0, "name", "ASC");
        Pageable pageableNegative = new Pageable(0, -10, "name", "ASC");

        assertThat(pageableZero.size()).isEqualTo(20);
        assertThat(pageableNegative.size()).isEqualTo(20);
    }

    @Test
    @DisplayName("Doit conserver les valeurs valides")
    void shouldKeepValidValues() {
        Pageable pageable = new Pageable(3, 50, "title", "ASC");

        assertThat(pageable.page()).isEqualTo(3);
        assertThat(pageable.size()).isEqualTo(50);
        assertThat(pageable.sortBy()).isEqualTo("title");
        assertThat(pageable.sortDirection()).isEqualTo("ASC");
    }

    @Test
    @DisplayName("Doit utiliser createdAt par défaut si sortBy est vide")
    void shouldDefaultSortByWhenBlank() {
        Pageable pageable = new Pageable(0, 10, "  ", "ASC");

        assertThat(pageable.sortBy()).isEqualTo("createdAt");
    }
}
