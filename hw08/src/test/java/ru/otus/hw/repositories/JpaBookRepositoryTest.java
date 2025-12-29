package ru.otus.hw.repositories;

import jakarta.persistence.PersistenceUnitUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA репозиторий комментариев")
@DataJpaTest
class JpaBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager tem;

    @DisplayName("должен загружать книгу по id и подгружать автора через EntityGraph")
    @Test
    void shouldFindById() {
        var bookOpt = bookRepository.findById(1L);
        assertThat(bookOpt).isPresent();
        var book = bookOpt.get();
        assertThat(book.getId()).isEqualTo(1L);
        assertThat(book.getTitle()).isEqualTo("BookTitle_1");

        PersistenceUnitUtil util = tem.getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();
        assertThat(util.isLoaded(book, "author"))
                .as("author должен быть загружен через fetchgraph")
                .isTrue();
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
        Author author = tem.find(Author.class, 1);
        Genre genre = tem.find(Genre.class, 1L);
        Book book = new Book(0, "New book", author, List.of(genre));
        Book saved = bookRepository.save(book);
        tem.flush();
        tem.clear();

        assertThat(saved.getId()).isPositive();

        var reloaded = bookRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getTitle()).isEqualTo("New book");
        assertThat(reloaded.getAuthor().getId()).isEqualTo(1L);
    }

    @DisplayName("Должен сохранять новый комментарий")
    @Test
    void shouldUpdateExistingBook() {
        Author author = tem.find(Author.class, 1);
        Genre genre = tem.find(Genre.class, 1L);
        Book book = new Book(1, "New book", author, List.of(genre));
        Book saved = bookRepository.save(book);

        tem.flush();
        tem.clear();

        assertThat(saved.getId()).isPositive();

        var reloaded = bookRepository.findById(1L).orElseThrow();
        assertThat(reloaded.getTitle()).isEqualTo("New book");
        assertThat(reloaded.getAuthor().getId()).isEqualTo(1L);
        assertThat(reloaded.getGenres()).extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre_1");
    }

    @DisplayName("Должен удалять книгу по id")
    @Test
    void shouldDeleteBookById() {
        assertThat(bookRepository.findById(1L)).isPresent();
        bookRepository.deleteById(1L);
        tem.flush();
        tem.clear();
        assertThat(bookRepository.findById(1L)).isNotPresent();
    }
}
