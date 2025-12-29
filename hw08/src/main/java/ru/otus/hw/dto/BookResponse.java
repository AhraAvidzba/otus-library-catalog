package ru.otus.hw.dto;

import java.util.List;

public record BookResponse(
        long id,
        String title,
        long authorId,
        String authorFullName,
        List<String> genres
) {}