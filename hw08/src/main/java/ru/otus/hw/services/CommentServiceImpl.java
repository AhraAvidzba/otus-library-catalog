package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.CommentResponse;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;


    @Override
    public Optional<CommentResponse> findById(String id) {
        return commentRepository.findById(id)
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getText(),
                        c.getBook().getId(),
                        c.getBook().getTitle()
                ));
    }

    @Override
    public List<CommentResponse> findByBookId(String bookId) {
        return commentRepository.findByBookId(bookId).stream()
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getText(),
                        c.getBook().getId(),
                        c.getBook().getTitle()
                ))
                .toList();
    }

    @Override
    public CommentResponse insert(String text, String bookId) {
        return save(null, text, bookId);
    }

    @Override
    public CommentResponse update(String id, String text, String bookId) {
        return save(id, text, bookId);
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
    }

    private CommentResponse save(String id, String text, String bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId))
        );
        var comment = new Comment(id, text, book);
        var saved = commentRepository.save(comment);
        return new CommentResponse(saved.getId(), saved.getText(), saved.getBook().getId(), saved.getBook().getTitle());
    }
}
