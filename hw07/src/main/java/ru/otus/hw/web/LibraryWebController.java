package ru.otus.hw.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class LibraryWebController {

    private final BookService bookService;
    private final CommentService commentService;
    private final AuthorService authorService;
    private final GenreService genreService;

    @GetMapping("/")
    public String index() {
        return "redirect:/books";
    }

    // -------- BOOKS --------

    @GetMapping("/books")
    public String books(Model model) {
        model.addAttribute("books", bookService.findAll());
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("newBook", new BookForm("", 0, Set.of()));
        return "books";
    }

    @PostMapping("/books")
    public String createBook(@ModelAttribute("newBook") BookForm form) {
        bookService.insert(form.title(), form.authorId(), form.genreIds());
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    @GetMapping("/books/{id}")
    public String bookDetails(@PathVariable long id, Model model) {
        BookDto book = bookService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        List<CommentDto> comments = commentService.findByBookId(id);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        model.addAttribute("newComment", new CommentForm("", id));
        return "book";
    }

    // Optional: simple update (title/author/genres) with a form on the book page.
    @GetMapping("/books/{id}/edit")
    public String editBook(@PathVariable long id, Model model) {
        BookDto book = bookService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("editBook", new BookForm(book.title(), book.authorId(), Set.of()));
        return "book-edit";
    }

    @PostMapping("/books/{id}/edit")
    public String updateBook(@PathVariable long id, @ModelAttribute("editBook") BookForm form) {
        bookService.update(id, form.title(), form.authorId(), form.genreIds());
        return "redirect:/books/" + id;
    }

    // -------- COMMENTS --------

    @PostMapping("/comments")
    public String addComment(@ModelAttribute("newComment") CommentForm form) {
        commentService.insert(form.text(), form.bookId());
        return "redirect:/books/" + form.bookId();
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable long id, @RequestParam("bookId") long bookId) {
        commentService.deleteById(id);
        return "redirect:/books/" + bookId;
    }

    // --- Forms ---

    public record BookForm(String title, long authorId, Set<Long> genreIds) {
    }

    public record CommentForm(String text, long bookId) {
    }
}
