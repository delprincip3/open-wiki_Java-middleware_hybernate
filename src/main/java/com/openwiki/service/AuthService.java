package com.openwiki.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AuthService {
    private static final String AUTH_API_URL = "http://127.0.0.1:5001";
    private static final String FLASK_AUTH_URL = "http://localhost:5001/auth/validate";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final OkHttpClient client;

    public AuthService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    public String login(String username, String password) throws Exception {
        try {
            ObjectNode jsonNode = objectMapper.createObjectNode();
            jsonNode.put("username", username);
            jsonNode.put("password", password);

            String requestBody = jsonNode.toString();
            String loginUrl = AUTH_API_URL + "/auth/login";
            System.out.println("Login Request URL: " + loginUrl);
            System.out.println("Login Request Body: " + requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(loginUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Login Response Status: " + response.statusCode());
            System.out.println("Login Response Body: " + response.body());
            
            if (response.statusCode() == 200) {
                return objectMapper.readTree(response.body()).get("token").asText();
            }
            throw new RuntimeException("Login failed with status " + response.statusCode());
        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
            throw new RuntimeException("Login failed: " + e.getMessage());
        }
    }

    public boolean validateToken(String token) throws Exception {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AUTH_API_URL + "/auth/validate"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            System.err.println("Auth Error: " + e.getMessage());
            return false;
        }
    }

    public ObjectNode getUserInfo(String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_API_URL + "/auth/user"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return (ObjectNode) objectMapper.readTree(response.body());
        }
        throw new RuntimeException("Failed to get user info: " + response.body());
    }

    public boolean validateFlaskSession(String sessionToken) {
        Request request = new Request.Builder()
            .url(FLASK_AUTH_URL)
            .header("Cookie", "session=" + sessionToken)
            .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful();
        } catch (Exception e) {
            return false;
        }
    }
} 