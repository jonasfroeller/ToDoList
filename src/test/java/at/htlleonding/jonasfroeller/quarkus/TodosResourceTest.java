package at.htlleonding.jonasfroeller.quarkus;

import at.htlleonding.jonasfroeller.quarkus.boundary.ToDoResource;
import at.htlleonding.jonasfroeller.quarkus.model.ToDo;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(ToDoResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodosResourceTest {
    @Test
    @Order(0)
    void testListReturnsTodosInCorrectOrder() {
        given()
                .when().get("list")
                .then()
                .body("size()", is(5))
                .body("description[0]", is("Change tires."))
                .body("deadline[0]", is("2023-10-31"))
                .body("priority[0]", is(1))
                .body("description[1]", is("Feed cats."))
                .body("deadline[1]", is("2023-11-03"))
                .body("priority[1]", is(3))
                .body("description[2]", is("Do laundry."))
                .body("deadline[2]", is("2023-11-05"))
                .body("priority[2]", is(2))
                .body("description[3]", is("Bake christmas cookies."))
                .body("deadline[3]", is("2023-12-24"))
                .body("priority[3]", is(1))
                .body("description[4]", is("Prepare exam questions."))
                .body("deadline[4]", is("2024-06-05"))
                .body("priority[4]", is(3));
    }

    @Test
    @Order(0)
    void testListFilteredByPriorityReturnsCorrectTodos() {
        given()
                .when().get("list/1")
                .then()
                .body("size()", is(2))
                .body("description", hasItems("Bake christmas cookies.", "Change tires."));

        given()
                .when().get("list/2")
                .then()
                .body("size()", is(1))
                .body("description", hasItems("Do laundry."));

        given()
                .when().get("list/3")
                .then()
                .body("size()", is(2))
                .body("description", hasItems("Feed cats.", "Prepare exam questions."));
    }

    @Test
    @Order(0)
    void testGetTodoByNonExistingIdReturnsNotFound() {
        int idMax = given()
                .when().get("list")
                .then()
                .extract()
                .path("id.max()");

        idMax++;

        given()
                .when().get(Integer.toString(idMax))
                .then()
                .statusCode(404);
    }

    @Test
    @Order(0)
    void testGetTodoByIdReturnsCorrectTodo() {
        ToDo[] todosExpected = given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class);

        for (ToDo currTodoExpected : todosExpected) {
            ToDo todoActual = given()
                    .when().get("" + currTodoExpected.getId())
                    .then()
                    .extract()
                    .as(ToDo.class);

            assertEquals(currTodoExpected.getDescription(), todoActual.getDescription());
            assertEquals(currTodoExpected.getDeadline(), todoActual.getDeadline());
            assertEquals(currTodoExpected.getPriority(), todoActual.getPriority());
        }
    }

    @Test
    @Order(1)
    void testAddTodoWithPriorityTooLowReturnsError() {
        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Wash car.")
                .add("deadline", "2023-11-04")
                .add("priority", 0)
                .build();

        String body = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post()
                .then()
                .statusCode(400)
                .extract()
                .body()
                .asString();

        assertTrue(body.contains("Priority must be between 1 and 3!"));
    }

    @Test
    @Order(1)
    void testAddTodoWithPriorityTooLowDoesNotChangeList() {
        List<ToDo> todosExpected = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Wash car.")
                .add("deadline", "2023-11-04")
                .add("priority", 0)
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post();

        List<ToDo> todosActual = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertEquals(todosExpected.size(), todosActual.size());

        assertFalse(todosActual.stream().anyMatch(t -> t.getDescription().equals("Wash car.")));
    }

    @Test
    @Order(1)
    void testAddTodoWithPriorityTooHighReturnsError() {
        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Buy christmas presents.")
                .add("deadline", "2023-12-23")
                .add("priority", 4)
                .build();

        String body = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post()
                .then()
                .statusCode(400)
                .extract()
                .body()
                .asString();

        assertTrue(body.contains("Priority must be between 1 and 3!"));
    }

    @Test
    @Order(1)
    void testAddTodoWithPriorityTooHighDoesNotChangeList() {
        List<ToDo> todosExpected = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Buy christmas presents.")
                .add("deadline", "2023-12-23")
                .add("priority", 4)
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post();

        List<ToDo> todosActual = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertEquals(todosExpected.size(), todosActual.size());

        assertFalse(todosActual.stream().anyMatch(t -> t.getDescription().equals("Buy christmas presents.")));
    }

    @Test
    @Order(1)
    void testAddTodoReturnsCorrectStatusCodeAndUrl() {
        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Buy football tickets.")
                .add("deadline", "2023-11-12")
                .add("priority", 3)
                .build();

        String headerLocation = given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post()
                .then()
                .statusCode(201)
                .extract()
                .header("Location");

        ToDo todo = given()
                .when().get(headerLocation)
                .then()
                .extract()
                .as(ToDo.class);

        assertEquals("Buy football tickets.", todo.getDescription());
        assertEquals(LocalDate.of(2023, 11, 12), todo.getDeadline());
        assertEquals(3, todo.getPriority());
    }

    @Test
    @Order(1)
    void testAddTodoChangesList() {
        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Return empty bottles.")
                .add("deadline", "2023-11-03")
                .add("priority", 1)
                .build();

        List<ToDo> todosExpected = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertFalse(todosExpected.stream().anyMatch(t -> t.getDescription().equals("Return empty bottles.")));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post()
                .then()
                .statusCode(201);

        List<ToDo> todosActual = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertEquals(todosExpected.size() + 1, todosActual.size());
        assertTrue(todosActual.stream().anyMatch(t -> t.getDescription().equals("Return empty bottles.")));
    }

    @Test
    @Order(1)
    void testAddTodoChangesFilteredList() {
        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Prepare Quarkus demo.")
                .add("deadline", "2023-11-06")
                .add("priority", 2)
                .build();

        List<ToDo> todosLowBefore = Arrays.asList(given()
                .when().get("list/1")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosMediumBefore = Arrays.asList(given()
                .when().get("list/2")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosHighBefore = Arrays.asList(given()
                .when().get("list/3")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertFalse(todosLowBefore.stream().anyMatch(t -> t.getDescription().equals("Prepare Quarkus demo.")));
        assertFalse(todosMediumBefore.stream().anyMatch(t -> t.getDescription().equals("Prepare Quarkus demo.")));
        assertFalse(todosHighBefore.stream().anyMatch(t -> t.getDescription().equals("Prepare Quarkus demo.")));

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post()
                .then()
                .statusCode(201);

        List<ToDo> todosLowAfter = Arrays.asList(given()
                .when().get("list/1")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosMediumAfter = Arrays.asList(given()
                .when().get("list/2")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosHighAfter = Arrays.asList(given()
                .when().get("list/3")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertEquals(todosLowBefore.size(), todosLowAfter.size());
        assertEquals(todosMediumBefore.size() + 1, todosMediumAfter.size());
        assertEquals(todosHighBefore.size(), todosHighAfter.size());
        assertFalse(todosLowAfter.stream().anyMatch(t -> t.getDescription().equals("Prepare Quarkus demo.")));
        assertTrue(todosMediumAfter.stream().anyMatch(t -> t.getDescription().equals("Prepare Quarkus demo.")));
        assertFalse(todosHighAfter.stream().anyMatch(t -> t.getDescription().equals("Prepare Quarkus demo.")));
    }

    @Test
    @Order(1)
    void testAddTodosListInCorrectOrder() {
        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Book a hotel.")
                .add("deadline", "2024-07-10")
                .add("priority", 2)
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post()
                .then()
                .statusCode(201);

        todoRaw = Json.createObjectBuilder()
                .add("description", "Cook delicious bigos.")
                .add("deadline", "2022-12-23")
                .add("priority", 3)
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post()
                .then()
                .statusCode(201);

        todoRaw = Json.createObjectBuilder()
                .add("description", "Buy new wok.")
                .add("deadline", "2022-11-30")
                .add("priority", 1)
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().post()
                .then()
                .statusCode(201);

        List<ToDo> todos = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        LocalDate deadlineLast = todos.get(0).getDeadline();

        for (int i = 1; i < todos.size(); i++) {
            LocalDate currDeadline = todos.get(i).getDeadline();
            assertTrue(deadlineLast.isBefore(currDeadline) || deadlineLast.isEqual(currDeadline));

            deadlineLast = currDeadline;
        }
    }

    @Test
    @Order(1)
    void testUpdateTodoByNonExistingIdReturnsNotFound() {
        int idMax = given()
                .when().get("list")
                .then()
                .extract()
                .path("id.max()");

        idMax++;

        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Cut toenails.")
                .add("deadline", "2023-11-07")
                .add("priority", 3)
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().put("" + idMax)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(1)
    void testUpdateTodoChangesTodo() {
        List<ToDo> todos = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        ToDo todo = given()
                .when().get("" + todos.get(1).getId())
                .then()
                .extract()
                .as(ToDo.class);

        assertNotEquals("Iron suit.", todo.getDescription());
        assertFalse(todo.getDeadline().isEqual(LocalDate.of(2023, 10, 16)));
        assertNotEquals(2, todo.getPriority());

        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Iron suit.")
                .add("deadline", "2023-10-16")
                .add("priority", 2)
                .build();

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().patch("" + todo.getId())
                .then()
                .statusCode(204);

        todo = given()
                .when().get("" + todo.getId())
                .then()
                .extract()
                .response()
                .as(ToDo.class);

        assertEquals("Iron suit.", todo.getDescription());
        assertTrue(todo.getDeadline().isEqual(LocalDate.of(2023, 10, 16)));
        assertEquals(2, todo.getPriority());
    }

    @Test
    @Order(1)
    void testUpdateTodoChangesList() {
        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Cuddle cats.")
                .add("deadline", "2023-11-04")
                .add("priority", 3)
                .build();

        List<ToDo> todosBefore = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertFalse(todosBefore.stream().anyMatch(t -> t.getDescription().equals("Cuddle cats.")));

        ToDo todo = todosBefore.get(2);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().put("" + todo.getId())
                .then()
                .statusCode(204);

        List<ToDo> todosAfter = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertEquals(todosBefore.size(), todosAfter.size());
        assertTrue(todosAfter.stream().anyMatch(t -> t.getDescription().equals("Cuddle cats.")));
    }

    @Test
    @Order(1)
    void testUpdateTodoChangesFilteredList() {
        JsonObject todoRaw = Json.createObjectBuilder()
                .add("description", "Repair vacuum.")
                .add("deadline", "2023-11-10")
                .add("priority", 2)
                .build();

        List<ToDo> todosLowBefore = Arrays.asList(given()
                .when().get("list/1")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosMediumBefore = Arrays.asList(given()
                .when().get("list/2")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosHighBefore = Arrays.asList(given()
                .when().get("list/3")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertFalse(todosLowBefore.stream().anyMatch(t -> t.getDescription().equals("Repair vacuum.")));
        assertFalse(todosMediumBefore.stream().anyMatch(t -> t.getDescription().equals("Repair vacuum.")));
        assertFalse(todosHighBefore.stream().anyMatch(t -> t.getDescription().equals("Repair vacuum.")));

        ToDo todo = todosLowBefore.get(1);

        given()
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRaw.toString())
                .when().put("" + todo.getId())
                .then()
                .statusCode(204);

        List<ToDo> todosLowAfter = Arrays.asList(given()
                .when().get("list/1")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosMediumAfter = Arrays.asList(given()
                .when().get("list/2")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosHighAfter = Arrays.asList(given()
                .when().get("list/3")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertEquals(todosLowBefore.size() - 1, todosLowAfter.size());
        assertEquals(todosMediumBefore.size() + 1, todosMediumAfter.size());
        assertEquals(todosHighBefore.size(), todosHighAfter.size());
        assertFalse(todosLowAfter.stream().anyMatch(t -> t.getDescription().equals("Repair vacuum.")));
        assertTrue(todosMediumAfter.stream().anyMatch(t -> t.getDescription().equals("Repair vacuum.")));
        assertFalse(todosHighAfter.stream().anyMatch(t -> t.getDescription().equals("Repair vacuum.")));
    }

    @Test
    @Order(2)
    void testDeleteTodoDeletesTodo() {
        List<ToDo> todosBefore = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        ToDo todo = todosBefore.get(3);

        given()
                .when().delete("" + todo.getId())
                .then()
                .statusCode(204);

        given()
                .when().get("" + todo.getId())
                .then()
                .statusCode(404);
    }

    @Test
    @Order(2)
    void testDeleteTodoByNonExistingIdReturnsNotFound() {
        int idMax = given()
                .when().get("list")
                .then()
                .extract()
                .path("id.max()");

        idMax++;

        given()
                .when().delete(Integer.toString(idMax))
                .then()
                .statusCode(404);
    }

    @Test
    @Order(2)
    void testDeleteTodoChangesList() {
        List<ToDo> todosBefore = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        ToDo todo = todosBefore.get(2);

        given()
                .when().delete("" + todo.getId())
                .then()
                .statusCode(204);

        List<ToDo> todosAfter = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertEquals(todosBefore.size() - 1, todosAfter.size());
        assertFalse(todosAfter.stream().anyMatch(t -> t.getDescription().equals(todo.getDescription())));
    }

    @Test
    @Order(2)
    void testDeleteTodoChangesFilteredList() {
        List<ToDo> todosLowBefore = Arrays.asList(given()
                .when().get("list/1")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosMediumBefore = Arrays.asList(given()
                .when().get("list/2")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosHighBefore = Arrays.asList(given()
                .when().get("list/3")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        ToDo todo = todosHighBefore.get(2);

        assertFalse(todosLowBefore.stream().anyMatch(t -> t.getDescription().equals(todo.getDescription())));
        assertFalse(todosMediumBefore.stream().anyMatch(t -> t.getDescription().equals(todo.getDescription())));
        assertTrue(todosHighBefore.stream().anyMatch(t -> t.getDescription().equals(todo.getDescription())));

        given()
                .when().delete("" + todo.getId())
                .then()
                .statusCode(204);

        List<ToDo> todosLowAfter = Arrays.asList(given()
                .when().get("list/1")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosMediumAfter = Arrays.asList(given()
                .when().get("list/2")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));
        List<ToDo> todosHighAfter = Arrays.asList(given()
                .when().get("list/3")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        assertEquals(todosLowBefore.size(), todosLowAfter.size());
        assertEquals(todosMediumBefore.size(), todosMediumAfter.size());
        assertEquals(todosHighBefore.size() - 1, todosHighAfter.size());
        assertFalse(todosLowAfter.stream().anyMatch(t -> t.getDescription().equals(todo.getDescription())));
        assertFalse(todosMediumAfter.stream().anyMatch(t -> t.getDescription().equals(todo.getDescription())));
        assertFalse(todosHighAfter.stream().anyMatch(t -> t.getDescription().equals(todo.getDescription())));
    }

    @Test
    @Order(99)
    void testListInCorrectOrderAfterAllTests() {
        List<ToDo> todos = Arrays.asList(given()
                .when().get("list")
                .then()
                .extract()
                .response()
                .as(ToDo[].class));

        LocalDate deadlineLast = todos.get(0).getDeadline();

        for (int i = 1; i < todos.size(); i++) {
            LocalDate currDeadline = todos.get(i).getDeadline();
            assertTrue(deadlineLast.isBefore(currDeadline) || deadlineLast.isEqual(currDeadline));

            deadlineLast = currDeadline;
        }
    }
}