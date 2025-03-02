package com.example.controller;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.model.User;
import com.example.service.UserService;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class UserController implements HttpHandler {
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
        String[] pathParts = path.split("/");
        
        // Log incoming request
        System.out.println(String.format("[%s] %s - %s", 
            java.time.LocalDateTime.now(), 
            method, 
            path));

        // Handle CORS preflight requests
        if (method.equals("OPTIONS")) {
            handleCorsPreflightRequest(exchange);
            return;
        }

        // Validate content type for requests with body
        if ((method.equals("POST") || method.equals("PUT")) && 
            !"application/json".equals(exchange.getRequestHeaders().getFirst("Content-Type"))) {
            sendResponse(exchange, 415, "{\"error\": \"Content-Type must be application/json\"}");
            return;
        }
        
        try {
            switch (method) {
                case "GET":
                    if (pathParts.length == 3) {
                        handleGetAllUsers(exchange);
                    } else if (pathParts.length == 4) {
                        handleGetUser(exchange, Long.parseLong(pathParts[3]));
                    } else {
                        sendResponse(exchange, 400, "{\"error\": \"Invalid path\"}");
                    }
                    break;
                case "POST":
                    if (pathParts.length == 3) {
                        handleCreateUser(exchange);
                    } else {
                        sendResponse(exchange, 400, "{\"error\": \"Invalid path\"}");
                    }
                    break;
                case "PUT":
                    if (pathParts.length == 4) {
                        handleUpdateUser(exchange, Long.parseLong(pathParts[3]));
                    } else {
                        sendResponse(exchange, 400, "{\"error\": \"Invalid path\"}");
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 4) {
                        handleDeleteUser(exchange, Long.parseLong(pathParts[3]));
                    } else {
                        sendResponse(exchange, 400, "{\"error\": \"Invalid path\"}");
                    }
                    break;
                default:
                    sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}");
            }
        } catch (NumberFormatException e) {
            sendResponse(exchange, 400, "{\"error\": \"Invalid user ID\"}");
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, "{\"error\": \"" + e.getMessage() + "\"}");
        } catch (IOException e) {
            System.err.println("Error processing request: " + e.getMessage());
            sendResponse(exchange, 400, "{\"error\": \"Error processing request\"}");
        } catch (Exception e) {
            System.err.println("Internal server error: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Internal server error\"}");
        }
    }

    private void handleCorsPreflightRequest(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "86400");
        exchange.sendResponseHeaders(204, -1);
    }

    private void handleGetAllUsers(HttpExchange exchange) throws IOException {
        List<User> userList = userService.getAllUsers();
        String response = objectMapper.writeValueAsString(userList);
        sendResponse(exchange, 200, response);
    }

    private void handleGetUser(HttpExchange exchange, Long id) throws IOException {
        User user = userService.getUser(id);
        if (user != null) {
            String response = objectMapper.writeValueAsString(user);
            sendResponse(exchange, 200, response);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
        }
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        User user = objectMapper.readValue(exchange.getRequestBody(), User.class);
        User createdUser = userService.createUser(user);
        String response = objectMapper.writeValueAsString(createdUser);
        sendResponse(exchange, 201, response);
    }

    private void handleUpdateUser(HttpExchange exchange, Long id) throws IOException {
        User userToUpdate = objectMapper.readValue(exchange.getRequestBody(), User.class);
        User updatedUser = userService.updateUser(id, userToUpdate);
        if (updatedUser != null) {
            String response = objectMapper.writeValueAsString(updatedUser);
            sendResponse(exchange, 200, response);
        } else {
            sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
        }
    }

    private void handleDeleteUser(HttpExchange exchange, Long id) throws IOException {
        if (userService.deleteUser(id)) {
            sendResponse(exchange, 204, "");
        } else {
            sendResponse(exchange, 404, "{\"error\": \"User not found\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        
        if (response.isEmpty()) {
            exchange.sendResponseHeaders(statusCode, -1);
        } else {
            byte[] responseBytes = response.getBytes("UTF-8");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
}