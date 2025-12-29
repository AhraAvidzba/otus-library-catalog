package ru.otus.hw.services;

import ru.otus.hw.dto.CommentResponse;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<CommentResponse> findById(long id);

    List<CommentResponse> findByBookId(long bookId);

    CommentResponse insert(String text, long bookId);

    CommentResponse update(long id, String text, long bookId);

    void deleteById(long id);
}
