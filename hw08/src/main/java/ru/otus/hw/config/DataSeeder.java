package ru.otus.hw.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;


@Component
@RequiredArgsConstructor
@Profile("!test") // чтобы в тестах не мешал (для тестов сделаем отдельный)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataSeeder implements CommandLineRunner {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    @Override
    public void run(String... args) {
        clearAll(); // 1) Чтобы при каждом запуске было "как в data.sql" и без дублей:

        List<Author> authors = seedAuthors(); // 2) Создаём авторов

        List<Genre> genres = seedGenres(); // 3) Жанры
        // 4) Книги + связи
        Book b1 = bookRepository.save(new Book(null,
                "BookTitle_1",
                authors.get(0),
                List.of(genres.get(0), genres.get(1))));
        Book b2 = bookRepository.save(new Book(null,
                "BookTitle_2",
                authors.get(1),
                List.of(genres.get(2), genres.get(3))));
        Book b3 = bookRepository.save(new Book(null,
                "BookTitle_3",
                authors.get(2),
                List.of(genres.get(4), genres.get(5))));
        // 5) Комментарии
        commentRepository.save(new Comment(null, "Comment_1", b1));
        commentRepository.save(new Comment(null, "Comment_2", b1));
        commentRepository.save(new Comment(null, "Comment_3", b3));
        commentRepository.save(new Comment(null, "Comment_4", b3));
    }

    private void clearAll() {
        commentRepository.deleteAll();
        bookRepository.deleteAll();
        genreRepository.deleteAll();
        authorRepository.deleteAll();
    }

    private List<Author> seedAuthors() {
        Author a1 = authorRepository.save(new Author(null, "Author_1"));
        Author a2 = authorRepository.save(new Author(null, "Author_2"));
        Author a3 = authorRepository.save(new Author(null, "Author_3"));
        return List.of(a1, a2, a3);
    }

    private List<Genre> seedGenres() {
        Genre g1 = genreRepository.save(new Genre(null, "Genre_1"));
        Genre g2 = genreRepository.save(new Genre(null, "Genre_2"));
        Genre g3 = genreRepository.save(new Genre(null, "Genre_3"));
        Genre g4 = genreRepository.save(new Genre(null, "Genre_4"));
        Genre g5 = genreRepository.save(new Genre(null, "Genre_5"));
        Genre g6 = genreRepository.save(new Genre(null, "Genre_6"));
        return List.of(g1, g2, g3, g4, g5, g6);
    }

    @PostConstruct
    public void init() {
        System.out.println(">>> DataInitRunner bean created");
    }
}