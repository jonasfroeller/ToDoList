package at.htlleonding.jonasfroeller.quarkus.boundary;

import at.htlleonding.jonasfroeller.quarkus.model.ToDo;
import at.htlleonding.jonasfroeller.quarkus.repository.ToDoListRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.util.List;

@Path("api/todos")
public class ToDoResource {
    @Context
    UriInfo uriInfo;
    @Inject
    ToDoListRepository toDoListRepository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    public Response addTodo(ToDo todo) {
        ToDo toDoCreated = this.toDoListRepository.addToDo(todo);

        UriBuilder uriBuilder = this.uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Long.toString(toDoCreated.getId()));

        return Response.created(uriBuilder.build()).build();
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    @Path("/{id}")
    public Response updateToDo(@PathParam("id") int id, ToDo toDo) {
        this.toDoListRepository.updateToDo(id, toDo);

        return Response.noContent().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    @Path("/{id}")
    public Response replaceToDo(@PathParam("id") int id, ToDo toDo) {
        this.toDoListRepository.replaceToDo(id, toDo);

        return Response.noContent().build();
    }

    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.MEDIA_TYPE_WILDCARD)
    @Path("/{id}")
    public Response removeToDo(@PathParam("id") int id) {
        this.toDoListRepository.removeToDo(id);

        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ToDo getToDo(@PathParam("id") int id) {
        return this.toDoListRepository.getToDo(id);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public List<ToDo> getAllToDos() {
        return this.toDoListRepository.getAllToDos();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/{priority}")
    public List<ToDo> getToDosFilteredByPriority(@PathParam("priority") int priority) {
        return this.toDoListRepository.getAllToDosHavingPriority(priority);
    }
}
