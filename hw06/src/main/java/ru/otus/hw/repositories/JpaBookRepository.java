package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBookRepository implements BookRepository {

    private final EntityManager em;

/*  TODO
    подумать над тем как не делать тяжелую загрузку всех жанров когда например подтягиваются книга
    для комментария в dto которого вообще не нужны жанры а только id и title книги */
    @Override
    public Optional<Book> findById(long id) {
        EntityGraph<?> eg = em.getEntityGraph("book-with-author");
        var query = em.createQuery("select b from Book b where b.id = :id", Book.class);
        query.setParameter("id", id);
        query.setHint("jakarta.persistence.fetchgraph", eg);
        Optional<Book> book = query.getResultList().stream().findFirst();
        book.ifPresent(b -> fetchGenresForBooks(List.of(b)));
        return book;
    }


    @Override
    public List<Book> findAll() {
        EntityGraph<?> eg = em.getEntityGraph("book-with-author");
        var query = em.createQuery("select b from Book b", Book.class);
        query.setHint("jakarta.persistence.fetchgraph", eg);
        List<Book> books = query.getResultList();
        fetchGenresForBooks(books); // private helper
        return books;
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

    private void fetchGenresForBooks(List<Book> books) {
        if (books == null || books.isEmpty()) {
            return;
        }

        List<Long> ids = books.stream()
                .map(Book::getId)
                .toList();

        em.createQuery(
                        "select distinct b from Book b " +
                                "left join fetch b.genres g " +
                                "where b.id in :ids", Book.class
                )
                .setParameter("ids", ids)
                .getResultList();
    }
}
