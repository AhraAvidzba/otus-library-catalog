package ru.otus.hw.dto;

public record CommentResponse(
        String id,
        String text,
        String bookId,
        String bookTitle
) {}
