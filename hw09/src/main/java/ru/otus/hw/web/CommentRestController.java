package ru.otus.hw.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.otus.hw.dto.CommentResponse;
import ru.otus.hw.dto.CommentUpsertRequest;
import ru.otus.hw.services.CommentService;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    /**
     * List comments for a book.
     */
    @GetMapping("/api/books/{bookId}/comments")
    public List<CommentResponse> findByBookId(@PathVariable String bookId) {
        return commentService.findByBookId(bookId);
    }

    /**
     * Create a comment for a book.
     */
    @PostMapping("/api/books/{bookId}/comments")
    public ResponseEntity<CommentResponse> create(@PathVariable String bookId,
                                                  @RequestBody CommentUpsertRequest request) {
        var created = commentService.insert(request.text(), bookId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/comments/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/api/comments/{id}")
    public CommentResponse findById(@PathVariable String id) {
        return commentService.findById(id)
                .orElseThrow(() -> new ru.otus.hw.exceptions.EntityNotFoundException(
                        "Comment with id %s not found".formatted(id)
                ));
    }

    /**
     * Update a comment. By design, we keep the comment bound to the same book.
     */
    @PutMapping("/api/comments/{id}")
    public CommentResponse update(@PathVariable String id, @RequestBody CommentUpsertRequest request) {
        var existing = commentService.findById(id)
                .orElseThrow(() -> new ru.otus.hw.exceptions.EntityNotFoundException(
                        "Comment with id %s not found".formatted(id)
                ));
        return commentService.update(id, request.text(), existing.bookId());
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
