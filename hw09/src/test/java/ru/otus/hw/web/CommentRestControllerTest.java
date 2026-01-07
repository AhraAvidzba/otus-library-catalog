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
import ru.otus.hw.dto.CommentResponse;
import ru.otus.hw.dto.CommentUpsertRequest;
import ru.otus.hw.services.CommentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentRestController.class)
@Import(RestExceptionHandler.class)
class CommentRestControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper om;

    @MockitoBean
    private CommentService commentService;

    @Test
    @DisplayName("GET /api/books/{bookId}/comments -> 200 + list")
    void shouldGetCommentsByBookId() throws Exception {
        given(commentService.findByBookId("b1")).willReturn(List.of(
                new CommentResponse("c1", "Nice!", "b1", "War and Peace"),
                new CommentResponse("c2", "Great book", "b1", "War and Peace")
        ));

        mvc.perform(get("/api/books/{bookId}/comments", "b1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("c1"))
                .andExpect(jsonPath("$[0].bookId").value("b1"));

        verify(commentService).findByBookId("b1");
    }

    @Test
    @DisplayName("POST /api/books/{bookId}/comments -> 201 + Location + created body")
    void shouldCreateCommentForBook() throws Exception {
        given(commentService.insert("Nice book!", "b1"))
                .willReturn(new CommentResponse("c1", "Nice book!", "b1", "War and Peace"));

        mvc.perform(post("/api/books/{bookId}/comments", "b1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(new CommentUpsertRequest("Nice book!"))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/comments/c1"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("c1"))
                .andExpect(jsonPath("$.text").value("Nice book!"))
                .andExpect(jsonPath("$.bookId").value("b1"));

        verify(commentService).insert("Nice book!", "b1");
    }

    @Test
    @DisplayName("GET /api/comments/{id} (found) -> 200 + comment")
    void shouldGetCommentById() throws Exception {
        given(commentService.findById("c1")).willReturn(Optional.of(
                new CommentResponse("c1", "Nice book!", "b1", "War and Peace")
        ));

        mvc.perform(get("/api/comments/{id}", "c1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("c1"))
                .andExpect(jsonPath("$.bookId").value("b1"));

        verify(commentService).findById("c1");
    }

    @Test
    @DisplayName("GET /api/comments/{id} (not found) -> 500 (если не маппишь на 404)")
    void shouldReturn500WhenCommentNotFound() throws Exception {
        given(commentService.findById("missing")).willReturn(Optional.empty());

        mvc.perform(get("/api/comments/{id}", "missing"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.path").value("/api/comments/missing"));

        verify(commentService).findById("missing");
    }
}