package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    @GET
    public Response getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return Response.ok(users).build();
        } catch (Exception e) {
            logger.error("Error getting all users", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long id) {
        try {
            User user = userService.getUser(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(new ErrorResponse("User not found"))
                             .build();
            }
            return Response.ok(user).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        } catch (Exception e) {
            logger.error("Error getting user with id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @POST
    public Response createUser(User user) {
        try {
            User createdUser = userService.createUser(user);
            return Response.status(Response.Status.CREATED)
                         .entity(createdUser)
                         .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            if (updatedUser == null) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(new ErrorResponse("User not found"))
                             .build();
            }
            return Response.ok(updatedUser).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        } catch (Exception e) {
            logger.error("Error updating user with id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            boolean deleted = userService.deleteUser(id);
            if (!deleted) {
                return Response.status(Response.Status.NOT_FOUND)
                             .entity(new ErrorResponse("User not found"))
                             .build();
            }
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        } catch (Exception e) {
            logger.error("Error deleting user with id: " + id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(new ErrorResponse(e.getMessage()))
                         .build();
        }
    }

    private static class ErrorResponse {
        private String error;

        public ErrorResponse(String message) {
            this.error = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
