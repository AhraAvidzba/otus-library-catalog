package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentResponse;

@Component
public class CommentConverter {
    public String commentToString(CommentResponse comment) {
        return "Id: %s, Book Id: %s, Book title: %s, text: %s".formatted(comment.id(), comment.bookId(),
                comment.bookTitle(), comment.text());
    }
}
