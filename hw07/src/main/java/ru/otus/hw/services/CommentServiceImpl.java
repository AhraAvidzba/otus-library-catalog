package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional(readOnly = true)
    public Optional<CommentResponse> findById(long id) {
        return commentRepository.findById(id)
                .map(c -> new CommentResponse(
                        c.getId(),
                        c.getText(),
                        c.getBook().getId(),
                        c.getBook().getTitle()
                ));
    }

    @Override
    @Transactional
    public List<CommentResponse> findByBookId(long bookId) {
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
    @Transactional
    public CommentResponse insert(String text, long bookId) {
        return save(0, text, bookId);
    }

    @Override
    @Transactional
    public CommentResponse update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private CommentResponse save(long id, String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId))
        );
        var comment = new Comment(id, text, book);
        var saved = commentRepository.save(comment);
        return new CommentResponse(saved.getId(), saved.getText(), saved.getBook().getId(), saved.getBook().getTitle());
    }
}
