package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.testdata.TestDataSeeder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Интеграционные тесты BookService")
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class BookServiceIT {

    @Autowired
    private BookService bookService;

    @Autowired
    TestDataSeeder seeder;

    @DisplayName("должен возвращать DTO книги со всеми нужными полями")
    @Test
    void shouldReturnBookDto() {
        var dtoOpt = bookService.findById("1");
        assertThat(dtoOpt).isPresent();

        var dto = dtoOpt.get();
        assertThat(dto.id()).isEqualTo("1");
        assertThat(dto.title()).isEqualTo("BookTitle_1");
        assertThat(dto.authorId()).isEqualTo("1");
        assertThat(dto.authorFullName()).isEqualTo("Author_1");
        assertThat(dto.genres()).containsExactlyInAnyOrder("Genre_1", "Genre_2");
    }

    @DisplayName("должен возвращать список всех книг")
    @Test
    void shouldReturnAllBooks() {
        var dtos = bookService.findAll();
        assertThat(dtos).hasSize(3);
    }

    @DisplayName("должен вставлять и читать новую книгу")
    @Test
    void shouldInsertBook() {
        var created = bookService.insert("InsertedBook", "2", Set.of("1", "3"));
        assertThat(created.id()).isNotEmpty();

        var loaded = bookService.findById(created.id()).orElseThrow();
        assertThat(loaded.title()).isEqualTo("InsertedBook");
        assertThat(loaded.authorId()).isEqualTo("2");
        assertThat(loaded.genres()).containsExactlyInAnyOrder("Genre_1", "Genre_3");
    }

    @DisplayName("должен обновлять существующую книгу")
    @Test
    void shouldUpdateBook() {
        var updated = bookService.update("1", "UpdatedBook", "3", Set.of("5", "6"));
        assertThat(updated.id()).isEqualTo("1");
        assertThat(updated.title()).isEqualTo("UpdatedBook");
        assertThat(updated.authorId()).isEqualTo("3");
        assertThat(updated.genres()).containsExactlyInAnyOrder("Genre_5", "Genre_6");

        var loaded = bookService.findById("1").orElseThrow();
        assertThat(loaded.title()).isEqualTo("UpdatedBook");
    }

    @DisplayName("должен удалять книгу")
    @Test
    void shouldDeleteBook() {
        assertThat(bookService.findById("2")).isPresent();
        bookService.deleteById("2");
        assertThat(bookService.findById("2")).isEmpty();
    }

    @BeforeEach
    void setUp() {
        seeder.seed();
    }
}
