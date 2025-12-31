package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.testdata.TestDataSeeder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA репозиторий комментариев")
@DataMongoTest
@ActiveProfiles("test")
@Import({TestDataSeeder.class})
class JpaBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    TestDataSeeder seeder;

    @DisplayName("должен загружать книгу по id и подгружать автора через EntityGraph")
    @Test
    void shouldFindById() {
        var bookOpt = bookRepository.findById("1");
        assertThat(bookOpt).isPresent();
        var book = bookOpt.get();
        assertThat(book.getId()).isEqualTo("1");
        assertThat(book.getTitle()).isEqualTo("BookTitle_1");
        assertThat(book.getAuthor().getFullName()).isEqualTo("Author_1");
    }

    @DisplayName("должен возвращать список всех книг")
    @Test
    void shouldFindAll() {
        var books = bookRepository.findAll();
        assertThat(books)
                .hasSize(3)
                .extracting(Book::getTitle)
                .containsExactlyInAnyOrder("BookTitle_1", "BookTitle_2", "BookTitle_3");
    }

    @DisplayName("Должен сохранять новую книгу")
    @Test
    void shouldInsertNewBook() {
        var author = mongoTemplate.findById("1", Author.class, "authors");
        var genre = mongoTemplate.findById("1", Genre.class, "genres");

        Book book = new Book(null, "New book", author, List.of(genre));
        Book saved = bookRepository.save(book);

        assertThat(saved.getId()).isNotEmpty();

        var reloaded = bookRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getTitle()).isEqualTo("New book");
        assertThat(reloaded.getAuthor().getId()).isEqualTo("1");
    }

    @DisplayName("Должен обновлять новую книгу")
    @Test
    void shouldUpdateExistingBook() {
        var author = mongoTemplate.findById("1", Author.class, "authors");
        var genre = mongoTemplate.findById("1", Genre.class, "genres");

        Book book = new Book("1", "New book", author, List.of(genre));
        Book saved = bookRepository.save(book);

        assertThat(saved.getId()).isNotEmpty();

        var reloaded = bookRepository.findById("1").orElseThrow();
        assertThat(reloaded.getTitle()).isEqualTo("New book");
        assertThat(reloaded.getAuthor().getId()).isEqualTo("1");
        assertThat(reloaded.getGenres()).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre_1");
    }

    @DisplayName("Должен удалять книгу по id")
    @Test
    void shouldDeleteBookById() {
        assertThat(bookRepository.findById("1")).isPresent();
        bookRepository.deleteById("1");
        assertThat(bookRepository.findById("1")).isNotPresent();
    }

    @BeforeEach
    void setUp() {
        seeder.seed();
    }
}
