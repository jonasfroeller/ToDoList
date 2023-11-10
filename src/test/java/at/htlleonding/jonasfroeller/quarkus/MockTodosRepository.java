package at.htlleonding.jonasfroeller.quarkus;

import at.htlleonding.jonasfroeller.quarkus.model.*;
import at.htlleonding.jonasfroeller.quarkus.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

import java.time.LocalDate;

@ApplicationScoped
@Alternative
@Priority(999)
public class MockTodosRepository extends ToDoListRepository {
    @PostConstruct
    public void init() {
        ToDo todoCookies = new ToDo();
        todoCookies.setDescription("Bake christmas cookies.");
        todoCookies.setDeadline(LocalDate.of(2023, 12, 24));
        todoCookies.setPriority(1);

        this.addToDo(todoCookies);

        ToDo todoCats = new ToDo();
        todoCats.setDescription("Feed cats.");
        todoCats.setDeadline(LocalDate.of(2023, 11, 3));
        todoCats.setPriority(3);

        this.addToDo(todoCats);

        ToDo todoExams = new ToDo();
        todoExams.setDescription("Prepare exam questions.");
        todoExams.setDeadline(LocalDate.of(2024, 6, 5));
        todoExams.setPriority(3);

        this.addToDo(todoExams);

        ToDo todoTires = new ToDo();
        todoTires.setDescription("Change tires.");
        todoTires.setDeadline(LocalDate.of(2023, 10, 31));
        todoTires.setPriority(1);

        this.addToDo(todoTires);

        ToDo todoLaundry = new ToDo();
        todoLaundry.setDescription("Do laundry.");
        todoLaundry.setDeadline(LocalDate.of(2023, 11, 5));
        todoLaundry.setPriority(2);

        this.addToDo(todoLaundry);
    }
}
