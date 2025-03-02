package com.example.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthController {
    
    @GET
    public Response healthCheck() {
        return Response.ok("{\"status\": \"UP\"}").build();
    }
}