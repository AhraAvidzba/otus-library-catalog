package ru.otus.hw.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class DataInitRunner implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    @Override
    public void run(String... args) {
        System.out.println(">>> DataInitRunner started");
        // 1) Чтобы при каждом запуске было "как в data.sql" и без дублей:
        commentRepository.deleteAll();
        bookRepository.deleteAll();
        genreRepository.deleteAll();
        authorRepository.deleteAll();

        // 2) Создаём авторов (id назначится автоматически твоим MongoIdListener'ом)
        Author a1 = authorRepository.save(new Author(0, "Author_1"));
        Author a2 = authorRepository.save(new Author(0, "Author_2"));
        Author a3 = authorRepository.save(new Author(0, "Author_3"));

        // 3) Жанры
        Genre g1 = genreRepository.save(new Genre(0, "Genre_1"));
        Genre g2 = genreRepository.save(new Genre(0, "Genre_2"));
        Genre g3 = genreRepository.save(new Genre(0, "Genre_3"));
        Genre g4 = genreRepository.save(new Genre(0, "Genre_4"));
        Genre g5 = genreRepository.save(new Genre(0, "Genre_5"));
        Genre g6 = genreRepository.save(new Genre(0, "Genre_6"));

        // 4) Книги + связи
        Book b1 = bookRepository.save(new Book(0, "BookTitle_1", a1, List.of(g1, g2)));
        Book b2 = bookRepository.save(new Book(0, "BookTitle_2", a2, List.of(g3, g4)));
        Book b3 = bookRepository.save(new Book(0, "BookTitle_3", a3, List.of(g5, g6)));

        // 5) Комментарии
        commentRepository.save(new Comment(0, "Comment_1", b1));
        commentRepository.save(new Comment(0, "Comment_2", b1));
        commentRepository.save(new Comment(0, "Comment_3", b3));
        commentRepository.save(new Comment(0, "Comment_4", b3));

        System.out.println("Authors count = " + authorRepository.count());
        System.out.println("Genres count = " + genreRepository.count());
        System.out.println("Books count = " + bookRepository.count());
        System.out.println("Comments count = " + commentRepository.count());
    }

    @PostConstruct
    public void init() {
        System.out.println(">>> DataInitRunner bean created");
    }
}