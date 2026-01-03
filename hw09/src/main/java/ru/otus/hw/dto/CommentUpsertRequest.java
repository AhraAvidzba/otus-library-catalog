package ru.otus.hw.dto;

/**
 * Request body for creating/updating a comment via REST.
 *
 * Example:
 * {
 *   "text": "Nice book!"
 * }
 */
public record CommentUpsertRequest(String text) {
}
