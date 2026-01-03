package ru.otus.hw.dto;

import java.util.Set;

/**
 * Request body for creating/updating a book via REST.
 */
public record BookUpsertRequest(
        String title,
        String authorId,
        Set<String> genreIds
) {
}
