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
import ru.otus.hw.dto.AuthorCreateRequest;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthorRestController.class)
@Import(RestExceptionHandler.class)
class AuthorRestControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @DisplayName("GET /api/authors -> 200 + list")
    void shouldGetAuthors() throws Exception {
        given(authorService.findAll()).willReturn(List.of(
                new Author("a1", "Leo Tolstoy"),
                new Author("a2", "Fyodor Dostoevsky")
        ));

        mvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("a1"))
                .andExpect(jsonPath("$[0].fullName").value("Leo Tolstoy"));

        verify(authorService).findAll();
    }

    @Test
    @DisplayName("POST /api/authors (valid) -> 201 + created author")
    void shouldCreateAuthor() throws Exception {
        given(authorService.create("Leo Tolstoy"))
                .willReturn(new Author("a1", "Leo Tolstoy"));

        mvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new AuthorCreateRequest("  Leo Tolstoy  "))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("a1"))
                .andExpect(jsonPath("$.fullName").value("Leo Tolstoy"));

        verify(authorService).create("Leo Tolstoy");
    }

    @Test
    @DisplayName("POST /api/authors (blank fullName) -> 400")
    void shouldRejectCreateAuthorWhenBlank() throws Exception {
        mvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new AuthorCreateRequest("   "))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/authors"));
    }

    @Test
    @DisplayName("DELETE /api/authors/{id} -> 204")
    void shouldDeleteAuthor() throws Exception {
        doNothing().when(authorService).deleteById("a1");

        mvc.perform(delete("/api/authors/{id}", "a1"))
                .andExpect(status().isNoContent());

        verify(authorService).deleteById("a1");
    }
}