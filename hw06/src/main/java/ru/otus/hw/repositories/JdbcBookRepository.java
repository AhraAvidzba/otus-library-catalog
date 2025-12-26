package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.FETCH;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final EntityManager em;
    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> eg = em.getEntityGraph("book.author.genres.graph");
        var query = em.createQuery("select b from Book b where id = :id", Book.class);
        query.setParameter("id", id);
        query.setHint(FETCH.getKey(), eg);
        return query.getResultList().stream().findFirst();
    }


    @Override
    public List<Book> findAll() {
        EntityGraph<?> eg = em.getEntityGraph("book.author.genres.graph");
        var query = em.createQuery("select b from Book b", Book.class);
        query.setHint(FETCH.getKey(), eg);
        return query.getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;              // book становится managed и id появится после flush/commit
        }
        return em.merge(book);        // вернет managed-объект (НЕ тот же самый экземпляр)
    }

    @Override
    public void deleteById(long id) {
        Book book = em.find(Book.class, id);
        if (book == null) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(id));
        }
        em.remove(book);
    }
}
