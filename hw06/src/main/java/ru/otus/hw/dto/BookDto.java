package ru.otus.hw.dto;

import java.util.List;

public record BookDto(
        long id,
        String title,
        long authorId,
        String authorFullName,
        List<String> genres
) {}