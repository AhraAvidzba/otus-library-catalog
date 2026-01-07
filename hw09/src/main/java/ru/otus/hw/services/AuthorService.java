package ru.otus.hw.services;

import ru.otus.hw.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> findAll();

    Optional<Author> findById(String id);

    Author create(String fullName);

    Author update(String id, String name);

    void deleteById(String id);
}

