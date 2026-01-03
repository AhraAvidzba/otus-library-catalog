package ru.otus.hw.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import ru.otus.hw.dto.BookResponse;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.web.mvc.dto.BookForm;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookMvcController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final CommentService commentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "lib/book/booklist";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable("id") String id,
                       @RequestParam(value = "editCommentId", required = false) String editCommentId,
                       Model model) {
        BookResponse book = bookService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        model.addAttribute("book", book);
        model.addAttribute("comments", commentService.findByBookId(id));
        model.addAttribute("editCommentId", editCommentId);

        return "lib/book/bookview";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("bookform", new BookForm());
        fillReferenceData(model);
        return "lib/book/newbookform";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute("bookform") BookForm bookform) {
        bookService.insert(bookform.getTitle(), bookform.getAuthorId(), safeSet(bookform.getGenreIds()));
        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") String id, Model model) {
        BookResponse book = bookService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        BookForm bookform = new BookForm();
        bookform.setId(book.id());
        bookform.setTitle(book.title());
        bookform.setAuthorId(book.authorId());

        // BookResponse содержит только названия жанров, поэтому сопоставляем названия -> id.
        List<Genre> allGenres = genreService.findAll();
        Set<String> selectedGenreIds = allGenres.stream()
                .filter(g -> book.genres() != null && book.genres().contains(g.getName()))
                .map(Genre::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        bookform.setGenreIds(selectedGenreIds);

        model.addAttribute("bookform", bookform);
        fillReferenceData(model, allGenres);
        return "lib/book/newbookform";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") String id, @ModelAttribute("bookform") BookForm bookform) {
        bookService.update(id, bookform.getTitle(), bookform.getAuthorId(), safeSet(bookform.getGenreIds()));
        return "redirect:/books";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") String id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }






    @PostMapping("/{bookId}/comments/create")
    public String createComment(@PathVariable("bookId") String bookId,
                                @RequestParam("text") String text) {
        commentService.insert(text, bookId);
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/{bookId}/comments/{commentId}/edit")
    public String updateComment(@PathVariable("bookId") String bookId,
                                @PathVariable("commentId") String commentId,
                                @RequestParam("text") String text) {
        commentService.update(commentId, text, bookId);
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/{bookId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable("bookId") String bookId,
                                @PathVariable("commentId") String commentId) {
        commentService.deleteById(commentId);
        return "redirect:/books/" + bookId;
    }


    private void fillReferenceData(Model model) {
        fillReferenceData(model, genreService.findAll());
    }

    private void fillReferenceData(Model model, List<Genre> genres) {
        List<Author> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        model.addAttribute("genres", genres);
    }

    private Set<String> safeSet(Set<String> ids) {
        return (ids == null) ? Set.of() : ids;
    }
}
