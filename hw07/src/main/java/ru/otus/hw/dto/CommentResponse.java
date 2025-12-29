package ru.otus.hw.dto;

public record CommentResponse(
        long id,
        String text,
        long bookId,
        String bookTitle
) {}
