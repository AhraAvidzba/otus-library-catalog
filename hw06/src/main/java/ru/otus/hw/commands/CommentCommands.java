package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find comments by bookId", key = "cbbid")
    public String findCommentByBookId(long bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find comment by id", key = "cbid")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Insert comment", key = "insCom")
    public String insertComment(String text, long bookId) {
        var savedComment = commentService.insert(text, bookId);
        return commentConverter.commentToString(savedComment);
    }

    // bupd 4 editedBook 3 2,5
    @ShellMethod(value = "Update comment", key = "updCom")
    public String updateComment(long id, String text, long BookId) {
        var savedComment = commentService.update(id, text, BookId);
        return commentConverter.commentToString(savedComment);
    }

    // bdel 4
    @ShellMethod(value = "Delete comment by id", key = "delCom")
    public void deleteBook(long id) {
        commentService.deleteById(id);
    }
}
