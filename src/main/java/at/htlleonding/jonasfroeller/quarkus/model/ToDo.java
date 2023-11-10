package at.htlleonding.jonasfroeller.quarkus.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity

@NamedQueries({
        @NamedQuery(name = ToDo.QUERY_GET_ALL, query = "SELECT t FROM ToDo t ORDER BY t.deadline"),
        @NamedQuery(name = ToDo.QUERY_GET_ALL_HAVING_PRIORITY, query = "SELECT t FROM ToDo t WHERE priority = :priority ORDER BY t.deadline"),
        @NamedQuery(name = ToDo.QUERY_GET_USING_ID, query = "SELECT t FROM ToDo t WHERE id = :id")
})

public class ToDo { // POJO :)
    public static final String QUERY_GET_USING_ID = "ToDo.getID";
    public static final String QUERY_GET_ALL = "ToDo.getAll";
    public static final String QUERY_GET_ALL_HAVING_PRIORITY = "ToDo.getAll.havingPriority";
    public static final int TODO_PRIORITY_MIN = 1;
    public static final int TODO_PRIORITY_MAX = 3;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String description;
    private LocalDate deadline;
    private int priority;

    public ToDo() {
    }

    public void update(ToDo toDo) {
        setDescription(toDo.description);
        setDeadline(toDo.deadline);
        setPriority(toDo.priority);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        if (priority >= TODO_PRIORITY_MIN && priority <= TODO_PRIORITY_MAX) {
            this.priority = priority;
        } else {
            throw new IllegalArgumentException("Priority must be between 1 and 3!");
        }
    }

    public Long getId() {
        return id;
    }
}
