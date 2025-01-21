package com.openwiki;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;
import com.openwiki.config.DatabaseConfig;
import com.openwiki.dao.ArticleDAO;
import java.sql.Connection;
import com.openwiki.controller.WikiController;

import com.openwiki.controller.AuthController;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;



public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> {
                cors.add(it -> {
                    it.reflectClientOrigin = true;
                    it.allowCredentials = true;
                    it.maxAge = 86400;
                });
            });
            
            config.plugins.enableDevLogging();
            config.http.defaultContentType = "application/json";
            // Configura Jackson per gestire LocalDateTime
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> 
                mapper.registerModule(new JavaTimeModule())
            ));
        });

        // Log all requests
        app.before(ctx -> {
            System.out.println("Incoming request: " + ctx.method() + " " + ctx.path());
            System.out.println("Headers: " + ctx.headerMap());
        });

        // Error handlers
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("error", e.getMessage()));
        });

        app.error(404, ctx -> {
            ctx.json(Map.of("error", "Not found: " + ctx.path()));
        });

        WikiController wikiController = new WikiController();
        AuthController authController = new AuthController();

        // Auth endpoints (non protetti)
        app.routes(() -> {
            app.post("/api/auth/login", ctx -> {
                System.out.println("Login endpoint hit");
                authController.login(ctx);
            });
            app.get("/api/auth/user", authController::getUserInfo);
        });

        // Test endpoints (non protetti)
        app.get("/api/test", Main::testHandler);
        app.get("/api/test/db", Main::testDatabase);

        // Configura gli endpoint senza protezione
        app.post("/api/articles", wikiController::saveArticle);
        app.get("/api/articles", wikiController::getUserArticles);
        app.delete("/api/articles/{id}", wikiController::deleteArticle);
        app.put("/api/articles/{id}", wikiController::updateArticle);

        // Endpoint Wikipedia (senza autenticazione)
        app.get("/api/wikipedia/search", wikiController::search);
        app.get("/api/wikipedia/article/{title}", wikiController::getArticle);
        app.get("/api/wikipedia/featured", wikiController::getFeaturedArticle);

        // Rimuovi la protezione degli endpoint Wikipedia
        // app.before("/api/wikipedia/*", new AuthMiddleware());

        app.start(8080);
    }

    private static void testHandler(Context ctx) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "OpenWiki Middleware is running!");
        response.put("status", "success");
        ctx.json(response);
    }
    
    private static void testDatabase(Context ctx) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Test connessione database
            Connection conn = DatabaseConfig.getConnection();
            conn.close();
            
            // Test ArticleDAO con un ID utente valido
            ArticleDAO articleDAO = new ArticleDAO();
            articleDAO.findByUserId("1"); // Usa "1" invece di "test-user"
            
            response.put("message", "Database connection and queries working!");
            response.put("status", "success");
            ctx.json(response);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Database test failed: " + e.getMessage());
            ctx.status(500).json(response);
        }
    }
} 