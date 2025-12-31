package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.DbSequence;

/**
 * Генератор автоинкрементных long id для MongoDB.
 * Делает findAndModify с $inc:1 (атомарно).
 */
@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {

    private final MongoOperations mongoOperations;

    /**
     * @param sequenceName имя последовательности, например "authors_seq"
     * @return следующее значение (1,2,3,...)
     */
    public long getNext(String sequenceName) {
        Query query = new Query(Criteria.where("_id").is(sequenceName));
        Update update = new Update().inc("seq", 1);

        // upsert(true) - если записи ещё нет, создаст её
        // returnNew(true) - вернуть уже увеличенное значение
        FindAndModifyOptions options = FindAndModifyOptions.options()
                .upsert(true)
                .returnNew(true);

        DbSequence counter = mongoOperations.findAndModify(query, update, options, DbSequence.class);

        // теоретически counter может быть null только при совсем нестандартной проблеме
        if (counter == null) {
            throw new IllegalStateException("Could not get sequence for: " + sequenceName);
        }

        return counter.getSeq();
    }
}