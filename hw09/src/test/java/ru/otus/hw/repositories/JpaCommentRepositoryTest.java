package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.testdata.TestDataSeeder;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA репозиторий комментариев")
@DataMongoTest
@Import({TestDataSeeder.class})

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
        var commentOpt = commentRepository.findById("1");
        assertThat(commentOpt).isPresent();
        var comment = commentOpt.get();
        assertThat(comment.getId()).isEqualTo("1");
        assertThat(comment.getText()).isEqualTo("Comment_1");
        assertThat(comment.getBook().getTitle()).isEqualTo("BookTitle_1");
    }

    @DisplayName("должен возвращать все комментарии по id книги")
    @Test
    void shouldFindAll() {
        var comments = commentRepository.findByBookId("1");
        assertThat(comments)
                .hasSize(2)
                .extracting(Comment::getText)
                .containsExactlyInAnyOrder("Comment_1", "Comment_2");
        assertThat(comments)
                .allMatch(c -> c.getBook() != null && c.getBook().getId().equals( "1"));
    }

    @DisplayName("Должен сохранять новый комментарий")
    @Test
    void shouldInsertNewComment() {
        Book book = mongoTemplate.findById("1", Book.class, "books");
        Comment comment = new Comment(null,"я в восторге", book);
        Comment saved = commentRepository.save(comment);

        assertThat(saved.getId()).isNotEmpty();

        var reloaded = commentRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getText()).isEqualTo("я в восторге");
        assertThat(reloaded.getBook().getId()).isEqualTo("1");
    }

    @DisplayName("Должен обновлять существующий комментарий")
    @Test
    void shouldUpdateExistingComment() {
        Book book = mongoTemplate.findById("3", Book.class, "books");
        Comment comment = new Comment("1","я в восторге", book);
        Comment saved = commentRepository.save(comment);

        assertThat(saved.getId()).isNotEmpty();

        var reloaded = commentRepository.findById("1").orElseThrow();
        assertThat(reloaded.getText()).isEqualTo("я в восторге");
        assertThat(reloaded.getBook().getId()).isEqualTo("3");
        assertThat(reloaded.getBook().getTitle()).isEqualTo("BookTitle_3");
    }

    @DisplayName("Должен удалять комментарий по id")
    @Test
    void shouldDeleteCommentById() {
        assertThat(commentRepository.findById("1")).isPresent();
        commentRepository.deleteById("1");
        assertThat(commentRepository.findById("1")).isNotPresent();
    }

    @BeforeEach
    void setUp() {
        seeder.seed();
    }
}
