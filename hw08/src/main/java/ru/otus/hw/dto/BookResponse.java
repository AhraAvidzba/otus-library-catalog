package ru.otus.hw.dto;

import java.util.List;

public record BookResponse(
        String id,
        String title,
        String authorId,
        String authorFullName,
        List<String> genres
) {}