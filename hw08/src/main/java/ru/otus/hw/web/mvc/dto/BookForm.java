package ru.otus.hw.web.mvc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Form-backing bean for Thymeleaf create/edit pages.
 */
@Setter
@Getter
public class BookForm {

    private String id;
    private String title;
    private String authorId;
    private Set<String> genreIds = new LinkedHashSet<>();

    public BookForm(){}


    public void setGenreIds(Set<String> genreIds) {
        this.genreIds = (genreIds == null) ? new LinkedHashSet<>() : genreIds;
    }
}
