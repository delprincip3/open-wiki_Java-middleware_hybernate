package com.openwiki.controller;

import io.javalin.http.Context;
import com.openwiki.service.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

public class AuthController {
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public AuthController() {
        this.authService = new AuthService();
        this.objectMapper = new ObjectMapper();
    }

    public void login(Context ctx) {
        try {
            JsonNode json = objectMapper.readTree(ctx.body());
            String username = json.get("username").asText();
            String password = json.get("password").asText();

            String token = authService.login(username, password);
            
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            ctx.json(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed: " + e.getMessage());
            ctx.status(401).json(error);
        }
    }

    public void getUserInfo(Context ctx) {
        try {
            String token = ctx.header("Authorization").substring(7);
            ctx.json(authService.getUserInfo(token));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get user info: " + e.getMessage());
            ctx.status(500).json(error);
        }
    }

    public void register(Context ctx) {
        // Implementare la registrazione
    }
} 