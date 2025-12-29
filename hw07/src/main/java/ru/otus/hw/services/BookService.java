package ru.otus.hw.services;

import ru.otus.hw.dto.BookResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookService {
    Optional<BookResponse> findById(long id);

    List<BookResponse> findAll();

    BookResponse insert(String title, long authorId, Set<Long> genresIds);

    BookResponse update(long id, String title, long authorId, Set<Long> genresIds);

    void deleteById(long id);

    class CommentService {

    }
}
