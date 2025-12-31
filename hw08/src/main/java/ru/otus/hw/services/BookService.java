package ru.otus.hw.services;

import ru.otus.hw.dto.BookResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookResponse> findById(String id);

    List<BookResponse> findAll();

    BookResponse insert(String title, String authorId, Set<String> genresIds);

    BookResponse update(String id, String title, String authorId, Set<String> genresIds);

    void deleteById(String id);

    class CommentService {

    }
}
