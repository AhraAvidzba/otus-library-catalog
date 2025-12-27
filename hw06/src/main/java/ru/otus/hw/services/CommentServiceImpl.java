package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(c -> new CommentDto(
                        c.getId(),
                        c.getText(),
                        c.getBook().getId(),
                        c.getBook().getTitle()
                ));
    }
    @Override
    @Transactional
    public List<CommentDto> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId).stream()
                .map(c -> new CommentDto(
                        c.getId(),
                        c.getText(),
                        c.getBook().getId(),
                        c.getBook().getTitle()
                ))
                .toList();
    }
}
