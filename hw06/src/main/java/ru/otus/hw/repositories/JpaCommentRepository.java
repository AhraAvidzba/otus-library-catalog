package ru.otus.hw.repositories;

import jakarta.persistence.EntityGraph;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaCommentRepository implements CommentRepository{

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        EntityGraph<?> eg = em.getEntityGraph("book.graph");
        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.id = :id", Comment.class);
        query.setParameter("id", id);
        query.setHint("jakarta.persistence.fetchgraph", eg);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Comment> findByBookId(long bookId) {
        EntityGraph<?> eg = em.getEntityGraph("book.graph");
        TypedQuery<Comment> query = em.createQuery("select c from Comment c where c.book.id = :id", Comment.class);
        query.setParameter("id", bookId);
        query.setHint("jakarta.persistence.fetchgraph", eg);
        return query.getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;              // book становится managed и id появится после flush/commit
        }
        return em.merge(comment);        // вернет managed-объект (НЕ тот же самый экземпляр)
    }

    @Override
    public void deleteById(long id) {
        Comment comment = em.find(Comment.class, id);
        if (comment == null) {
            throw new EntityNotFoundException("Comment with id %d not found".formatted(id));
        }
        em.remove(comment);
    }
}
