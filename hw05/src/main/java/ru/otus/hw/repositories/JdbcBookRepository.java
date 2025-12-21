package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Book> findById(long id) {
        String sql = """
                select b.id        as book_id,
                       b.title     as book_title,
                       a.id        as author_id,
                       a.full_name as author_name,
                       g.id        as genre_id,
                       g.name      as genre_name
                from books b
                         join authors a on b.author_id = a.id
                         left join books_genres bg on b.id = bg.book_id
                         left join genres g on bg.genre_id = g.id
                where b.id = :id
                order by g.id
                """;
        var params = Map.of("id", id);
        Book book = jdbc.query(sql, params, new BookResultSetExtractor());
        return Optional.ofNullable(book);
    }


    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        String sql = "delete from books where id = :id";
        jdbc.update(sql, Map.of("id", id));
    }


    private List<Book> getAllBooksWithoutGenres() {
        String sql = """
                select b.id        as book_id,
                       b.title     as book_title,
                       a.id        as author_id,
                       a.full_name as author_name
                from books b
                         join authors a on b.author_id = a.id
                order by b.id
                """;
        return jdbc.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        String sql = "select book_id, genre_id from books_genres order by book_id, genre_id";
        return jdbc.query(sql, (rs, rowNum) ->
                new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id")));
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        if (booksWithoutGenres.isEmpty() || genres.isEmpty() || relations.isEmpty()) {
            return;
        }
        Map<Long, Book> bookById = new HashMap<>();
        for (Book b : booksWithoutGenres) {
            bookById.put(b.getId(), b);
        }
        Map<Long, Genre> genreById = new HashMap<>();
        for (Genre g : genres) {
            genreById.put(g.getId(), g);
        }
        for (BookGenreRelation rel : relations) {
            Book book = bookById.get(rel.bookId());
            Genre genre = genreById.get(rel.genreId());
            if (book != null && genre != null) {
                book.getGenres().add(genre);
            }
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        String sql = "insert into books(title, author_id) values (:title, :authorId)";
        var params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId());

        jdbc.update(sql, params, keyHolder, new String[]{"id"});

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        String sql = "update books set title = :title, author_id = :authorId where id = :id";
        var params = new MapSqlParameterSource()
                .addValue("id", book.getId())
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId());
        int updated = jdbc.update(sql, params);
        if (updated == 0) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<Genre> genres = book.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        String sql = "insert into books_genres(book_id, genre_id) values (:bookId, :genreId)";
        MapSqlParameterSource[] batchParams = genres.stream()
                .map(g -> new MapSqlParameterSource()
                        .addValue("bookId", book.getId())
                        .addValue("genreId", g.getId()))
                .toArray(MapSqlParameterSource[]::new);
        jdbc.batchUpdate(sql, batchParams);
    }

    private void removeGenresRelationsFor(Book book) {
        String sql = "delete from books_genres where book_id = :bookId";
        jdbc.update(sql, Map.of("bookId", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            String title = rs.getString("book_title");
            long authorId = rs.getLong("author_id");
            String authorName = rs.getString("author_name");

            Author author = new Author(authorId, authorName);
            return new Book(bookId, title, author, new ArrayList<>());
        }
    }

    // Использовать для findById
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            List<Genre> genres = new ArrayList<>();

            while (rs.next()) {
                if (book == null) {
                    long bookId = rs.getLong("book_id");
                    String title = rs.getString("book_title");
                    long authorId = rs.getLong("author_id");
                    String authorName = rs.getString("author_name");
                    Author author = new Author(authorId, authorName);
                    book = new Book(bookId, title, author, genres);
                }

                long genreId = rs.getLong("genre_id");
                if (!rs.wasNull()) {
                    String genreName = rs.getString("genre_name");
                    genres.add(new Genre(genreId, genreName));
                }
            }

            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
