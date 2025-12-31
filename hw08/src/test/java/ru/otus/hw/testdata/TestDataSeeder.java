package ru.otus.hw.testdata;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataSeeder {

    private final MongoTemplate mongoTemplate;

    public void seed() {
        // чистим, чтобы каждый тест начинался одинаково
        mongoTemplate.dropCollection("comments");
        mongoTemplate.dropCollection("books");
        mongoTemplate.dropCollection("genres");
        mongoTemplate.dropCollection("authors");
        mongoTemplate.dropCollection("database_sequences"); // если используешь sequence-генерацию id

        // authors
        var a1 = new Author("1", "Author_1");
        var a2 = new Author("2", "Author_2");
        var a3 = new Author("3", "Author_3");
        mongoTemplate.insert(a1, "authors");
        mongoTemplate.insert(a2, "authors");
        mongoTemplate.insert(a3, "authors");

        // genres
        var g1 = new Genre("1", "Genre_1");
        var g2 = new Genre("2", "Genre_2");
        var g3 = new Genre("3", "Genre_3");
        var g4 = new Genre("4", "Genre_4");
        var g5 = new Genre("5", "Genre_5");
        var g6 = new Genre("6", "Genre_6");
        mongoTemplate.insert(g1, "genres");
        mongoTemplate.insert(g2, "genres");
        mongoTemplate.insert(g3, "genres");
        mongoTemplate.insert(g4, "genres");
        mongoTemplate.insert(g5, "genres");
        mongoTemplate.insert(g6, "genres");

        // books (author + genres — как у тебя в data.sql)
        var b1 = new Book("1", "BookTitle_1", a1, List.of(g1, g2));
        var b2 = new Book("2", "BookTitle_2", a2, List.of(g3, g4));
        var b3 = new Book("3", "BookTitle_3", a3, List.of(g5, g6));
        mongoTemplate.insert(b1, "books");
        mongoTemplate.insert(b2, "books");
        mongoTemplate.insert(b3, "books");

        // comments
        mongoTemplate.insert(new Comment("1", "Comment_1", b1), "comments");
        mongoTemplate.insert(new Comment("2", "Comment_2", b1), "comments");
        mongoTemplate.insert(new Comment("3", "Comment_3", b3), "comments");
        mongoTemplate.insert(new Comment("4", "Comment_4", b3), "comments");
    }
}