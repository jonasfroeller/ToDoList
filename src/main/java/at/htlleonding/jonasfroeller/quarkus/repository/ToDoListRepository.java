package at.htlleonding.jonasfroeller.quarkus.repository;

import at.htlleonding.jonasfroeller.quarkus.model.ToDo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;

@ApplicationScoped
public class ToDoListRepository {
    @Inject
    EntityManager entityManager;

    @Transactional
    public ToDo addToDo(ToDo toDo) {
        if (toDo != null) {
            this.entityManager.persist(toDo);
            return toDo;
        }

        return null;
    }

    @Transactional
    public void updateToDo(int id, ToDo toDo) {
        if (toDo != null && id >= 0) {
            ToDo foundToDo = this.entityManager.find(ToDo.class, id);

            if (foundToDo != null) {
                ToDo existing = entityManager.merge(foundToDo);
                existing.update(toDo);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new IllegalArgumentException("Invalid ToDo input!");
        }
    }

    @Transactional
    public void replaceToDo(int id, ToDo toDo) {
        if (toDo != null && id >= 0) {
            ToDo foundToDo = this.entityManager.find(ToDo.class, id);

            if (foundToDo != null) {
                this.entityManager.remove(foundToDo);
                this.entityManager.persist(toDo);
            } else {
                throw new NotFoundException();
            }
        } else {
            throw new IllegalArgumentException("Invalid ToDo input!");
        }
    }

    @Transactional
    public void removeToDo(int id) {
        ToDo toDo = this.entityManager.find(ToDo.class, id);

        if (toDo != null) {
            this.entityManager.remove(toDo);
        } else {
            throw new NotFoundException();
        }
    }

    public ToDo getToDo(int id) {
        try {
            TypedQuery<ToDo> query = this.entityManager.createNamedQuery(ToDo.QUERY_GET_USING_ID, ToDo.class);
            query.setParameter("id", id);

            return query.getSingleResult();
        } catch (Exception e) {
            throw new NotFoundException();
        }
    }

    public List<ToDo> getAllToDos() {
        return this.entityManager.createNamedQuery(ToDo.QUERY_GET_ALL, ToDo.class).getResultList();
    }

    public List<ToDo> getAllToDosHavingPriority(int priority) {
        TypedQuery<ToDo> query = this.entityManager.createNamedQuery(ToDo.QUERY_GET_ALL_HAVING_PRIORITY, ToDo.class);
        query.setParameter("priority", priority);

        return query.getResultList();
    }
}
