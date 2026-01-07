package ru.otus.hw.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import ru.otus.hw.dto.BookResponse;
import ru.otus.hw.dto.BookUpsertRequest;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookRestController.class)
@Import(RestExceptionHandler.class)
class BookRestControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockitoBean
    private BookService bookService;

    @Test
    @DisplayName("GET /api/books -> 200 + list")
    void shouldGetBooks() throws Exception {
        given(bookService.findAll()).willReturn(List.of(
                new BookResponse("b1", "War and Peace", "a1", "Leo Tolstoy", List.of("Novel", "Epic"))
        ));

        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("b1"))
                .andExpect(jsonPath("$[0].title").value("War and Peace"));

        verify(bookService).findAll();
    }

    @Test
    @DisplayName("GET /api/books/{id} (found) -> 200 + book")
    void shouldGetBookById() throws Exception {
        given(bookService.findById("b1")).willReturn(Optional.of(
                new BookResponse("b1", "War and Peace", "a1", "Leo Tolstoy", List.of("Novel"))
        ));

        mvc.perform(get("/api/books/{id}", "b1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("b1"))
                .andExpect(jsonPath("$.title").value("War and Peace"));

        verify(bookService).findById("b1");
    }

    @Test
    @DisplayName("GET /api/books/{id} (not found) -> 500 (если не маппишь на 404)")
    void shouldReturn500WhenBookNotFound() throws Exception {
        given(bookService.findById("missing")).willReturn(Optional.empty());

        mvc.perform(get("/api/books/{id}", "missing"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.path").value("/api/books/missing"));

        verify(bookService).findById("missing");
    }

    @Test
    @DisplayName("POST /api/books -> 201 + Location + created body")
    void shouldCreateBook() throws Exception {
        given(bookService.insert("War and Peace", "a1", Set.of("g1", "g2")))
                .willReturn(new BookResponse("b1", "War and Peace", "a1", "Leo Tolstoy", List.of("Novel", "Epic")));

        mvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new BookUpsertRequest(
                                "War and Peace",
                                "a1",
                                Set.of("g1", "g2")
                        ))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/books/b1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("b1"));

        verify(bookService).insert("War and Peace", "a1", Set.of("g1", "g2"));
    }

    @Test
    @DisplayName("PUT /api/books/{id} -> 200 + updated body")
    void shouldUpdateBook() throws Exception {
        given(bookService.update("b1", "New Title", "a1", Set.of("g1")))
                .willReturn(new BookResponse("b1", "New Title", "a1", "Leo Tolstoy", List.of("Novel")));

        mvc.perform(put("/api/books/{id}", "b1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new BookUpsertRequest(
                                "New Title",
                                "a1",
                                Set.of("g1")
                        ))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("b1"))
                .andExpect(jsonPath("$.title").value("New Title"));

        verify(bookService).update("b1", "New Title", "a1", Set.of("g1"));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} -> 204")
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteById("b1");

        mvc.perform(delete("/api/books/{id}", "b1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteById("b1");
    }
}