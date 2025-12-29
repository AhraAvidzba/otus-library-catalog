package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"book"})
    @NonNull
    Optional<Comment> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"book"})
    List<Comment> findByBookId(Long bookId);
}
