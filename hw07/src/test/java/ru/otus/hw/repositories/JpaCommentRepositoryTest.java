package ru.otus.hw.repositories;

import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA репозиторий комментариев")
@DataJpaTest
class JpaCommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TestEntityManager tem;

    @DisplayName("должен находить комментарий по id и подгружать книгу через EntityGraph")
    @Test
    void shouldFindById() {
        var commentOpt = commentRepository.findById(1L);
        assertThat(commentOpt).isPresent();
        var comment = commentOpt.get();
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getText()).isEqualTo("Классная книга!");

        PersistenceUnitUtil util = tem.getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();
        assertThat(util.isLoaded(comment, "book"))
                .as("book должен быть загружен через fetchgraph")
                .isTrue();
        assertThat(comment.getBook().getTitle()).isEqualTo("BookTitle_1");
    }

    @DisplayName("должен возвращать все комментарии по id книги")
    @Test
    void shouldFindAll() {
        var comments = commentRepository.findByBookId(1L);
        assertThat(comments)
                .hasSize(2)
                .extracting(Comment::getText)
                .containsExactlyInAnyOrder("Классная книга!", "Ну такое...");
        assertThat(comments)
                .allMatch(c -> c.getBook() != null && c.getBook().getId() == 1L);
    }

    @DisplayName("Должен сохранять новый комментарий")
    @Test
    void shouldInsertNewComment() {
        Book book = tem.find(Book.class, 1L);
        Comment comment = new Comment(0,"я в восторге", book);
        Comment saved = commentRepository.save(comment);

        tem.flush();
        tem.clear();

        assertThat(saved.getId()).isPositive();

        var reloaded = commentRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getText()).isEqualTo("я в восторге");
        assertThat(reloaded.getBook().getId()).isEqualTo(1L);
    }

    @DisplayName("Должен обновлять существующий комментарий")
    @Test
    void shouldUpdateExistingComment() {
        Book book = tem.find(Book.class, 3L);
        Comment comment = new Comment(1,"я в восторге", book);
        Comment saved = commentRepository.save(comment);

        tem.flush();
        tem.clear();

        assertThat(saved.getId()).isPositive();

        var reloaded = commentRepository.findById(1L).orElseThrow();
        assertThat(reloaded.getText()).isEqualTo("я в восторге");
        assertThat(reloaded.getBook().getId()).isEqualTo(3L);
        assertThat(reloaded.getBook().getTitle()).isEqualTo("BookTitle_3");
    }

    @DisplayName("Должен удалять комментарий по id")
    @Test
    void shouldDeleteCommentById() {
        assertThat(commentRepository.findById(1L)).isPresent();
        commentRepository.deleteById(1L);
        tem.flush();
        tem.clear();
        assertThat(commentRepository.findById(1L)).isNotPresent();
    }
}
