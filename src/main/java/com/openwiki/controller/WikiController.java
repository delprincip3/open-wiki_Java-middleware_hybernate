package com.openwiki.controller;
import io.javalin.http.Context;
import com.openwiki.service.WikiService;
import com.openwiki.dao.ArticleDAO;
import com.openwiki.model.Article;
import java.util.Map;
import java.util.List;
import java.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;

public class WikiController {
    private final WikiService wikiService;
    private final ArticleDAO articleDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(WikiController.class);

    public WikiController() {
        this.wikiService = new WikiService();
        this.articleDAO = new ArticleDAO();
    }

    public void search(Context ctx) {
        try {
            String query = ctx.queryParam("query");
            int limit = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);
            
            if (query == null || query.trim().isEmpty()) {
                ctx.status(400).json(Map.of("error", "Query parameter is required"));
                return;
            }

            ctx.json(wikiService.search(query, limit));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Search failed: " + e.getMessage()));
        }
    }

    public void getArticle(Context ctx) {
        try {
            String title = ctx.pathParam("title");
            Article article = wikiService.getArticle(title);
            
            ctx.json(article);
        } catch (Exception e) {
            ctx.status(500).json(Map.of("error", "Failed to get article: " + e.getMessage()));
        }
    }

    public void saveArticle(Context ctx) {
        try {
            String requestBody = ctx.body();
            logger.info("Received request body: {}", requestBody);
            
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            
            Article article = new Article();
            article.setTitle(jsonNode.get("title").asText());
            article.setContent(jsonNode.get("content").asText());
            
            // Gestisci entrambi i formati (camelCase e snake_case)
            String imageUrl = jsonNode.has("imageUrl") ? jsonNode.get("imageUrl").asText() :
                             jsonNode.has("image_url") ? jsonNode.get("image_url").asText() : null;
            String pageId = jsonNode.has("pageId") ? jsonNode.get("pageId").asText() :
                           jsonNode.has("page_id") ? jsonNode.get("page_id").asText() : null;
            String wikiUrl = jsonNode.has("wikiUrl") ? jsonNode.get("wikiUrl").asText() :
                            jsonNode.has("wiki_url") ? jsonNode.get("wiki_url").asText() : null;
            
            article.setImageUrl(imageUrl);
            article.setPageId(pageId);
            article.setWikiUrl(wikiUrl);
            
            String userId = extractUserId(ctx);
            article.setUserId(userId);
            article.setDateDownloaded(LocalDateTime.now());
            
            Article savedArticle = articleDAO.save(article);
            ctx.json(savedArticle);
            
        } catch (Exception e) {
            logger.error("Failed to save article: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Failed to save article: " + e.getMessage()));
        }
    }

    public void getUserArticles(Context ctx) {
        try {
            String userId = extractUserId(ctx);
            logger.info("Fetching articles for user {}", userId);
            List<Article> articles = articleDAO.findByUserId(userId);
            ctx.json(articles);
        } catch (Exception e) {
            logger.error("Failed to get user articles: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Failed to get user articles: " + e.getMessage()));
        }
    }

    public void getFeaturedArticle(Context ctx) {
        try {
            Article article = wikiService.getFeaturedArticle();
            
            // Assicurati che l'URL dell'immagine sia completo
            if (article.getImageUrl() != null && article.getImageUrl().startsWith("//")) {
                article.setImageUrl("https:" + article.getImageUrl());
            }
            
            // Log per debug
            logger.info("Featured article fields - imageUrl: {}, pageId: {}, wikiUrl: {}", 
                       article.getImageUrl(), article.getPageId(), article.getWikiUrl());
            
            ctx.json(article);
        } catch (Exception e) {
            logger.error("Failed to get featured article: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Failed to get featured article: " + e.getMessage()));
        }
    }

    public void deleteArticle(Context ctx) {
        try {
            String articleId = ctx.pathParam("id");
            String userId = extractUserId(ctx);
            logger.info("Deleting article {} for user {}", articleId, userId);
            boolean deleted = articleDAO.deleteArticle(articleId, userId);
            if (deleted) {
                logger.info("Article {} successfully deleted", articleId);
                ctx.status(204);
            } else {
                logger.warn("Article {} not found or not owned by user {}", articleId, userId);
                ctx.status(404).json(Map.of("error", "Article not found"));
            }
        } catch (Exception e) {
            logger.error("Failed to delete article: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Failed to delete article: " + e.getMessage()));
        }
    }

    public void updateArticle(Context ctx) {
        try {
            String articleId = ctx.pathParam("id");
            Article article = ctx.bodyAsClass(Article.class);
            String userId = extractUserId(ctx);
            
            article.setId(articleId);
            article.setUserId(userId);
            
            boolean updated = articleDAO.updateArticle(article);
            if (updated) {
                ctx.json(article);
            } else {
                ctx.status(404).json(Map.of("error", "Article not found or not owned by user"));
            }
            
        } catch (Exception e) {
            logger.error("Failed to update article: {}", e.getMessage(), e);
            ctx.status(500).json(Map.of("error", "Failed to update article: " + e.getMessage()));
        }
    }

    private String extractUserId(Context ctx) {
        try {
            String sessionCookie = ctx.cookie("session");
            if (sessionCookie == null) {
                logger.warn("No session cookie found");
                return "4";
            }

            // Estrai il payload dal cookie di sessione
            String[] parts = sessionCookie.split("\\.");
            if (parts.length < 2) {
                logger.warn("Invalid session token format");
                return "4";
            }

            // Decodifica il payload Base64
            String payload = parts[0];  // Usa la prima parte invece della seconda
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            String jsonStr = new String(decodedBytes, StandardCharsets.UTF_8);
            logger.info("Decoded session payload: {}", jsonStr);

            // Usa Jackson per il parsing JSON
            JsonNode jsonNode = objectMapper.readTree(jsonStr);
            if (jsonNode.has("user_id")) {
                String userId = jsonNode.get("user_id").asText();
                logger.info("Extracted user ID: {}", userId);
                return userId;
            }

            logger.warn("Could not find user_id in session payload");
            return "4";
        } catch (Exception e) {
            logger.error("Failed to extract user ID from session cookie: {}", e.getMessage());
            return "4";
        }
    }
} 