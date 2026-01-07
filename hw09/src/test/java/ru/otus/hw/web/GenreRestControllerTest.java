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
import ru.otus.hw.dto.GenreCreateRequest;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

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

@WebMvcTest(controllers = GenreRestController.class)
@Import(RestExceptionHandler.class)
class GenreRestControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockitoBean
    private GenreService genreService;

    @Test
    @DisplayName("GET /api/genres -> 200 + list")
    void shouldGetGenres() throws Exception {
        given(genreService.findAll()).willReturn(List.of(
                new Genre("g1", "Novel"),
                new Genre("g2", "Drama")
        ));

        mvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("g1"))
                .andExpect(jsonPath("$[0].name").value("Novel"));

        verify(genreService).findAll();
    }

    @Test
    @DisplayName("POST /api/genres (valid) -> 201 + created genre")
    void shouldCreateGenre() throws Exception {
        given(genreService.create("Novel"))
                .willReturn(new Genre("g1", "Novel"));

        mvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new GenreCreateRequest("  Novel  "))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("g1"))
                .andExpect(jsonPath("$.name").value("Novel"));

        verify(genreService).create("Novel");
    }

    @Test
    @DisplayName("POST /api/genres (blank name) -> 400")
    void shouldRejectCreateGenreWhenBlank() throws Exception {
        mvc.perform(post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new GenreCreateRequest("   "))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/api/genres"));
    }

    @Test
    @DisplayName("DELETE /api/genres/{id} -> 204")
    void shouldDeleteGenre() throws Exception {
        doNothing().when(genreService).deleteById("g1");

        mvc.perform(delete("/api/genres/{id}", "g1"))
                .andExpect(status().isNoContent());

        verify(genreService).deleteById("g1");
    }
}