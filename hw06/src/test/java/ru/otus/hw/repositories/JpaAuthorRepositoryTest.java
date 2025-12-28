package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA репозиторий авторов")
@DataJpaTest
@Import(JpaAuthorRepository.class)
class JpaAuthorRepositoryTest {

    @Autowired
    private AuthorRepository authorRepository;

    @DisplayName("должен находить автора по id")
    @Test
    void shouldFindById() {
        var authorOpt = authorRepository.findById(1L);
        assertThat(authorOpt).isPresent();
        var author = authorOpt.get();
        assertThat(author.getId()).isEqualTo(1L);
        assertThat(author.getFullName()).isEqualTo("Author_1");
    }

    @DisplayName("должен возвращать всех авторов")
    @Test
    void shouldFindAll() {
        var authors = authorRepository.findAll();
        assertThat(authors)
                .hasSize(3)
                .extracting(Author::getFullName)
                .containsExactlyInAnyOrder("Author_1", "Author_2", "Author_3");
    }
}
