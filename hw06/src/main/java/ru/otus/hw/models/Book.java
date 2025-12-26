package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
@NamedEntityGraph(name = "book.author.genres.graph",
        attributeNodes = {@NamedAttributeNode("author"),
                @NamedAttributeNode("genres")})
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="author_id", nullable = false)
    private Author author;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
            name="books_genres",
            joinColumns = @JoinColumn(name="book_id"),
            inverseJoinColumns = @JoinColumn(name="genre_id")
    )
    private List<Genre> genres;
}
