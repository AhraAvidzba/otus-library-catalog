package ru.otus.hw.services;

import ru.otus.hw.dto.CommentResponse;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    Optional<CommentResponse> findById(String id);

    List<CommentResponse> findByBookId(String bookId);

    CommentResponse insert(String text, String bookId);

    CommentResponse update(String id, String text, String bookId);

    void deleteById(String id);
}
