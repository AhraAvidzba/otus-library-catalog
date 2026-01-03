package ru.otus.hw.web.mvc.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Form-backing bean for Thymeleaf create/edit pages.
 */
@Setter
@Getter
public class AuthorForm {

    private String id;
    private String name;

}
