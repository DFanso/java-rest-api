package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class UserController implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController() {
        this.userService = new UserService();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        int responseCode = 200;

        try {
            switch (method) {
                case "GET":
                    if (path.matches("/api/users/\\d+")) {
                        Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
                        response = handleGetUser(id);
                        if (response == null) {
                            response = "{\"error\": \"User not found\"}";
                            responseCode = 404;
                        }
                    } else {
                        response = handleGetAllUsers();
                    }
                    break;

                case "POST":
                    response = handleCreateUser(exchange.getRequestBody());
                    responseCode = 201;
                    break;

                case "PUT":
                    if (path.matches("/api/users/\\d+")) {
                        Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
                        response = handleUpdateUser(id, exchange.getRequestBody());
                        if (response == null) {
                            response = "{\"error\": \"User not found\"}";
                            responseCode = 404;
                        }
                    } else {
                        response = "{\"error\": \"Invalid path\"}";
                        responseCode = 400;
                    }
                    break;

                case "DELETE":
                    if (path.matches("/api/users/\\d+")) {
                        Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
                        boolean deleted = userService.deleteUser(id);
                        responseCode = deleted ? 204 : 404;
                        response = deleted ? "" : "{\"error\": \"User not found\"}";
                    } else {
                        response = "{\"error\": \"Invalid path\"}";
                        responseCode = 400;
                    }
                    break;

                default:
                    response = "{\"error\": \"Method not allowed\"}";
                    responseCode = 405;
            }
        } catch (Exception e) {
            logger.error("Error processing request", e);
            response = "{\"error\": \"" + e.getMessage() + "\"}";
            responseCode = 400;
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = response.getBytes();
        exchange.sendResponseHeaders(responseCode, responseBytes.length == 0 ? -1 : responseBytes.length);
        
        if (responseBytes.length > 0) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    private String handleGetAllUsers() throws IOException {
        List<User> users = userService.getAllUsers();
        return objectMapper.writeValueAsString(users);
    }

    private String handleGetUser(Long id) throws IOException {
        User user = userService.getUser(id);
        return user != null ? objectMapper.writeValueAsString(user) : null;
    }

    private String handleCreateUser(InputStream requestBody) throws IOException {
        User user = objectMapper.readValue(requestBody, User.class);
        User createdUser = userService.createUser(user);
        return objectMapper.writeValueAsString(createdUser);
    }

    private String handleUpdateUser(Long id, InputStream requestBody) throws IOException {
        User user = objectMapper.readValue(requestBody, User.class);
        User updatedUser = userService.updateUser(id, user);
        return updatedUser != null ? objectMapper.writeValueAsString(updatedUser) : null;
    }
}
