package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.config.MongoIdListener;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.SequenceGeneratorService;
import ru.otus.hw.testdata.TestDataSeeder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA репозиторий комментариев")
@DataMongoTest
@Import({SequenceGeneratorService.class, MongoIdListener.class, TestDataSeeder.class})

class JpaCommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    TestDataSeeder seeder;

    @DisplayName("должен находить комментарий по id и подгружать книгу через EntityGraph")
    @Test
    void shouldFindById() {
        var commentOpt = commentRepository.findById(1L);
        assertThat(commentOpt).isPresent();
        var comment = commentOpt.get();
        assertThat(comment.getId()).isEqualTo(1L);
        assertThat(comment.getText()).isEqualTo("Comment_1");
        assertThat(comment.getBook().getTitle()).isEqualTo("BookTitle_1");
    }

    @DisplayName("должен возвращать все комментарии по id книги")
    @Test
    void shouldFindAll() {
        var comments = commentRepository.findByBookId(1L);
        assertThat(comments)
                .hasSize(2)
                .extracting(Comment::getText)
                .containsExactlyInAnyOrder("Comment_1", "Comment_2");
        assertThat(comments)
                .allMatch(c -> c.getBook() != null && c.getBook().getId() == 1L);
    }

    @DisplayName("Должен сохранять новый комментарий")
    @Test
    void shouldInsertNewComment() {
        Book book = mongoTemplate.findById(1L, Book.class, "books");
        Comment comment = new Comment(0,"я в восторге", book);
        Comment saved = commentRepository.save(comment);

        assertThat(saved.getId()).isPositive();

        var reloaded = commentRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getText()).isEqualTo("я в восторге");
        assertThat(reloaded.getBook().getId()).isEqualTo(1L);
    }

    @DisplayName("Должен обновлять существующий комментарий")
    @Test
    void shouldUpdateExistingComment() {
        Book book = mongoTemplate.findById(3L, Book.class, "books");
        Comment comment = new Comment(1,"я в восторге", book);
        Comment saved = commentRepository.save(comment);

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
        assertThat(commentRepository.findById(1L)).isNotPresent();
    }

    @BeforeEach
    void setUp() {
        seeder.seed();
    }
}
