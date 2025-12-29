package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentResponse;

@Component
public class CommentConverter {
    public String commentToString(CommentResponse comment) {
        return "Id: %d, Book Id: %d, Book title: %s, text: %s".formatted(comment.id(), comment.bookId(),
                comment.bookTitle(), comment.text());
    }
}
