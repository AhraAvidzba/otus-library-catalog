package ru.otus.hw.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document("comments")
public class Comment {

    @Id
    private String id;

    private String text;

    @DocumentReference
    private Book book;
}
