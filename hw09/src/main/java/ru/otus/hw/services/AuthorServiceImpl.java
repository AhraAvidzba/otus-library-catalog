package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Optional<Author> findById(String id) {
        return authorRepository.findById(id);
    }

    @Override
    public Author create(String fullName) {
        var author = new Author(null, fullName);
        return authorRepository.save(author);
    }

    @Override
    public Author update(String id, String name) {
        return authorRepository.save(new Author(id, name));
    }

    @Override
    public void deleteById(String id) {
        if (bookRepository.existsByAuthorId(id)) {
            throw new IllegalStateException("Нельзя удалить автора: у него есть книги");
        }
        authorRepository.deleteById(id);
    }
}
