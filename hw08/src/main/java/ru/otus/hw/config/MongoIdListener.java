package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.SequenceGeneratorService;

/**
 * Автоприсваивание long id для MongoDB перед конвертацией в документ (перед save).
 * Работает похоже на @GeneratedValue(IDENTITY) в JPA.
 */
@Component
@RequiredArgsConstructor
public class MongoIdListener extends AbstractMongoEventListener<Object> {

    private final SequenceGeneratorService sequenceGeneratorService;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        Object source = event.getSource();

        if (source instanceof Author author) {
            if (author.getId() == 0) {
                author.setId(sequenceGeneratorService.getNext(MongoSequences.AUTHORS_SEQ));
            }
            return;
        }

        if (source instanceof Genre genre) {
            if (genre.getId() == 0) {
                genre.setId(sequenceGeneratorService.getNext(MongoSequences.GENRES_SEQ));
            }
            return;
        }

        if (source instanceof Book book) {
            if (book.getId() == 0) {
                book.setId(sequenceGeneratorService.getNext(MongoSequences.BOOKS_SEQ));
            }
            return;
        }

        if (source instanceof Comment comment) {
            if (comment.getId() == 0) {
                comment.setId(sequenceGeneratorService.getNext(MongoSequences.COMMENTS_SEQ));
            }
        }
    }
}