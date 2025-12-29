package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentResponse;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Интеграционные тесты BookService")
@SpringBootTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentServiceIT {

    @Autowired
    private CommentService commentService;

    @DisplayName("должен возвращать DTO комментария")
    @Test
    void shouldReturnCommentDto() {
        var dtoOpt = commentService.findById(4L);
        assertThat(dtoOpt).isPresent();

        var dto = dtoOpt.get();
        assertThat(dto.id()).isEqualTo(4L);
        assertThat(dto.bookId()).isEqualTo(3L);
        assertThat(dto.text()).isEqualTo("Фигня какая-то :(");
        assertThat(dto.bookTitle()).isEqualTo("BookTitle_3");
    }

    @DisplayName("должен возвращать все комментарии по id книги")
    @Test
    void shouldReturnCommentsByBookId() {
        var dtos = commentService.findByBookId(1L);
        assertThat(dtos)
                .hasSize(2)
                .extracting(CommentResponse::text)
                .containsExactlyInAnyOrder("Классная книга!", "Ну такое...");
    }

    @DisplayName("должен вставлять новый комментарий")
    @Test
    void shouldInsertComment() {
        var created = commentService.insert("Inserted comment", 1L);
        assertThat(created.id()).isPositive();
        assertThat(created.bookTitle()).isEqualTo("BookTitle_1");

        var loaded = commentService.findById(created.id()).orElseThrow();
        assertThat(loaded.text()).isEqualTo("Inserted comment");
        assertThat(loaded.bookId()).isEqualTo(1L);
    }

    @DisplayName("должен обновлять существующий комментарий")
    @Test
    void shouldUpdateBook() {
        var updated = commentService.update(1L, "Updated text", 3L);
        assertThat(updated.id()).isEqualTo(1L);
        assertThat(updated.text()).isEqualTo("Updated text");
        assertThat(updated.bookId()).isEqualTo(3L);
        assertThat(updated.bookTitle()).isEqualTo("BookTitle_3");
    }

    @DisplayName("должен удалять комментарий")
    @Test
    void shouldDeleteBook() {
        assertThat(commentService.findById(2L)).isPresent();
        commentService.deleteById(2L);
        assertThat(commentService.findById(2L)).isEmpty();
    }
}
