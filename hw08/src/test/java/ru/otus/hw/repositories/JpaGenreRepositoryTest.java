package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.config.MongoIdListener;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.SequenceGeneratorService;
import ru.otus.hw.testdata.TestDataSeeder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA репозиторий жанров")
@DataMongoTest
@Import({SequenceGeneratorService.class, MongoIdListener.class, TestDataSeeder.class})
class JpaGenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    TestDataSeeder seeder;

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
    void shouldFindAllByIdIn() {
        var genres = genreRepository.findAllByIdIn(Set.of(1L, 3L, 6L));
        assertThat(genres)
                .hasSize(3)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre_1", "Genre_3", "Genre_6");
    }

    @DisplayName("должен возвращать пустой список для пустого набора id")
    @Test
    void shouldReturnEmptyForEmptyIds() {
        assertThat(genreRepository.findAllByIdIn(Set.of())).isEmpty();
        assertThat(genreRepository.findAllByIdIn(null)).isEmpty();
    }

    @BeforeEach
    void setUp() {
        seeder.seed();
    }
}
