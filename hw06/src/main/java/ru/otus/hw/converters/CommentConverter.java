package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.dto.CommentDto;

@Component
public class CommentConverter {
    public String commentToString(CommentDto comment) {
        return "Id: %d, Book Id: %d, Book title: %s, text: %s".formatted(comment.id(), comment.bookId(), comment.bookTitle(), comment.text());
    }
}
