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
import ru.otus.hw.dto.CommentResponse;
import ru.otus.hw.testdata.TestDataSeeder;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Интеграционные тесты CommentService")
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class CommentServiceIT {

    @Autowired
    private CommentService commentService;

    @Autowired
    TestDataSeeder seeder;

    @DisplayName("должен возвращать DTO комментария")
    @Test
    void shouldReturnCommentDto() {
        var dtoOpt = commentService.findById("4");
        assertThat(dtoOpt).isPresent();

        var dto = dtoOpt.get();
        assertThat(dto.id()).isEqualTo("4");
        assertThat(dto.bookId()).isEqualTo("3");
        assertThat(dto.text()).isEqualTo("Comment_4");
        assertThat(dto.bookTitle()).isEqualTo("BookTitle_3");
    }

    @DisplayName("должен возвращать все комментарии по id книги")
    @Test
    void shouldReturnCommentsByBookId() {
        var dtos = commentService.findByBookId("1");
        assertThat(dtos)
                .hasSize(2)
                .extracting(CommentResponse::text)
                .containsExactlyInAnyOrder("Comment_1", "Comment_2");
    }

    @DisplayName("должен вставлять новый комментарий")
    @Test
    void shouldInsertComment() {
        var created = commentService.insert("Inserted comment", "1");
        assertThat(created.id()).isNotEmpty();
        assertThat(created.bookTitle()).isEqualTo("BookTitle_1");

        var loaded = commentService.findById(created.id()).orElseThrow();
        assertThat(loaded.text()).isEqualTo("Inserted comment");
        assertThat(loaded.bookId()).isEqualTo("1");
    }

    @DisplayName("должен обновлять существующий комментарий")
    @Test
    void shouldUpdateBook() {
        var updated = commentService.update("1", "Updated text", "3");
        assertThat(updated.id()).isEqualTo("1");
        assertThat(updated.text()).isEqualTo("Updated text");
        assertThat(updated.bookId()).isEqualTo("3");
        assertThat(updated.bookTitle()).isEqualTo("BookTitle_3");
    }

    @DisplayName("должен удалять комментарий")
    @Test
    void shouldDeleteBook() {
        assertThat(commentService.findById("2")).isPresent();
        commentService.deleteById("2");
        assertThat(commentService.findById("2")).isEmpty();
    }

    @BeforeEach
    void setUp() {
        seeder.seed();
    }
}
