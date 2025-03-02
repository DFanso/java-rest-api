package com.example.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.example.controller.UserController;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHttpServer {
    private static final Logger logger = LoggerFactory.getLogger(SimpleHttpServer.class);
    private static final int PORT = 8000;
    private HttpServer server;

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        // Register context handlers
        UserController userController = new UserController();
        server.createContext("/api/users", userController);
        server.createContext("/api/health", new HealthCheckHandler());
        
        server.start();
        logger.info("Server is running on port {}", PORT);
    }

    static class HealthCheckHandler implements HttpHandler {
        private static final Logger logger = LoggerFactory.getLogger(HealthCheckHandler.class);
        
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logger.info("Health check requested from: {}", exchange.getRemoteAddress());
            String response = "{\"status\": \"UP\"}";
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public static void main(String[] args) {
        try {
            SimpleHttpServer server = new SimpleHttpServer();
            server.start();
        } catch (IOException e) {
            logger.error("Failed to start server", e);
        }
    }
}
