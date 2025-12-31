package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Документ-счётчик (аналог sequence в SQL).
 * В коллекции "database_sequences" будет храниться запись на каждую последовательность.
 *
 * _id = имя последовательности (например "authors_seq")
 * seq = текущее значение
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "database_sequences")
public class DbSequence {

    @Id
    private String id;   // имя последовательности

    private long seq;    // текущее значение
}