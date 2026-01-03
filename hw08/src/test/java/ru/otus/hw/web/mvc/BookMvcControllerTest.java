package ru.otus.hw.web.mvc;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.dto.BookResponse;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = {BookMvcController.class})
class BookMvcControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private AuthorService authorService;

    @MockBean
    private GenreService genreService;

    @MockBean
    private CommentService commentService;


    @Test
    void shouldRenderListPage() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(
                new BookResponse("b1", "T1", "a1", "A1", List.of("G1", "G2")),
                new BookResponse("b2", "T2", "a2", "A2", List.of())
        ));

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("lib/book/booklist"))
                .andExpect(model().attributeExists("books"));

        verify(bookService, times(1)).findAll();
    }

    @Test
    void shouldRenderViewPage() throws Exception {
        when(bookService.findById("b1"))
                .thenReturn(Optional.of(new BookResponse("b1", "T1", "a1", "A1", List.of("G1"))));

        mvc.perform(get("/books/b1"))
                .andExpect(status().isOk())
                .andExpect(view().name("lib/book/bookview"))
                .andExpect(model().attributeExists("book"));

        verify(bookService).findById("b1");
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        when(bookService.findById("missing")).thenReturn(Optional.empty());

        mvc.perform(get("/books/missing"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRenderCreateForm() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(
                new Author("a1", "Author 1"),
                new Author("a2", "Author 2")
        ));
        when(genreService.findAll()).thenReturn(List.of(
                new Genre("g1", "G1"),
                new Genre("g2", "G2")
        ));

        mvc.perform(get("/books/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("lib/book/newbookform"))
                .andExpect(model().attributeExists("bookform"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));

        verify(authorService).findAll();
        verify(genreService).findAll();
    }

    @Test
    void shouldCreateBookAndRedirectToList() throws Exception {
        mvc.perform(post("/books/create")
                        .param("title", "New book")
                        .param("authorId", "a1")
                        .param("genreIds", "g1", "g2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<String>> genresCaptor = ArgumentCaptor.forClass(Set.class);
        verify(bookService).insert(eq("New book"), eq("a1"), genresCaptor.capture());
        assertThat(genresCaptor.getValue()).containsExactlyInAnyOrder("g1", "g2");
    }

    @Test
    void shouldRenderEditForm() throws Exception {
        when(bookService.findById("b1"))
                .thenReturn(Optional.of(new BookResponse("b1", "T1", "a1", "A1", List.of("G1"))));
        when(authorService.findAll()).thenReturn(List.of(new Author("a1", "Author 1")));
        when(genreService.findAll()).thenReturn(List.of(
                new Genre("g1", "G1"),
                new Genre("g2", "G2")
        ));

        mvc.perform(get("/books/b1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("lib/book/newbookform"))
                .andExpect(model().attributeExists("bookform"))
                .andExpect(model().attributeExists("authors"))
                .andExpect(model().attributeExists("genres"));

        verify(bookService).findById("b1");
        verify(authorService).findAll();
        verify(genreService).findAll();
    }

    @Test
    void shouldUpdateBookAndRedirectToList() throws Exception {
        mvc.perform(post("/books/b1/edit")
                        .param("id", "b1")
                        .param("title", "Updated")
                        .param("authorId", "a2")
                        .param("genreIds", "g9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<String>> genresCaptor = ArgumentCaptor.forClass(Set.class);
        verify(bookService).update(eq("b1"), eq("Updated"), eq("a2"), genresCaptor.capture());
        assertThat(genresCaptor.getValue()).containsExactly("g9");
    }

    @Test
    void shouldDeleteBookAndRedirectToList() throws Exception {
        mvc.perform(post("/books/b1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).deleteById("b1");
    }
}
