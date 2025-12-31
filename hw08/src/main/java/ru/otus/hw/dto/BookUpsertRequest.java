package ru.otus.hw.dto;

import java.util.Set;

/**
 * Request body for creating/updating a book via REST.
 *
 * Example:
 * {
 *   "title": "Book title",
 *   "authorId": 1,
 *   "genreIds": [1,2]
 * }
 */
public record BookUpsertRequest(
        String title,
        String authorId,
        Set<String> genreIds
) {
}
