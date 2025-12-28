package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA репозиторий жанров")
@DataJpaTest
@Import(JpaGenreRepository.class)
class JpaGenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @DisplayName("должен возвращать все жанры")
    @Test
    void shouldFindAll() {
        var genres = genreRepository.findAll();
        assertThat(genres)
                .hasSize(6)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder(
                        "Genre_1", "Genre_2", "Genre_3", "Genre_4", "Genre_5", "Genre_6");
    }

    @DisplayName("должен находить жанры по набору id")
    @Test
    void shouldFindAllByIds() {
        var genres = genreRepository.findAllByIds(Set.of(1L, 3L, 6L));
        assertThat(genres)
                .hasSize(3)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre_1", "Genre_3", "Genre_6");
    }

    @DisplayName("должен возвращать пустой список для пустого набора id")
    @Test
    void shouldReturnEmptyForEmptyIds() {
        assertThat(genreRepository.findAllByIds(Set.of())).isEmpty();
        assertThat(genreRepository.findAllByIds(null)).isEmpty();
    }
}
