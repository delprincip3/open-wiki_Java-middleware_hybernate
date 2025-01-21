package com.openwiki.service;

import com.openwiki.model.WikiSearchResult;
import com.openwiki.model.Article;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiService {
    private static final String API_URL = "https://it.wikipedia.org/w/api.php";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(WikiService.class);

    public WikiService() {
        this.httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public List<WikiSearchResult> search(String query, int limit) throws Exception {
        String url = API_URL + "?action=query" +
                    "&list=search" +
                    "&srsearch=" + URLEncoder.encode(query, StandardCharsets.UTF_8) +
                    "&format=json" +
                    "&srlimit=" + limit;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("User-Agent", "OpenWiki/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        List<WikiSearchResult> results = new ArrayList<>();

        JsonNode searchResults = root.path("query").path("search");
        for (JsonNode result : searchResults) {
            WikiSearchResult searchResult = new WikiSearchResult();
            searchResult.setTitle(result.path("title").asText());
            searchResult.setExcerpt(result.path("snippet").asText());
            searchResult.setPageId(result.path("pageid").asText());
            searchResult.setUrl("https://it.wikipedia.org/wiki/" + 
                              URLEncoder.encode(result.path("title").asText(), StandardCharsets.UTF_8));
            results.add(searchResult);
        }

        return results;
    }

    public Article getFeaturedArticle() throws Exception {
        String url = API_URL + "?action=query" +
                    "&list=random" +
                    "&rnnamespace=0" +
                    "&rnlimit=1" +
                    "&format=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("User-Agent", "OpenWiki/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        String title = root.path("query").path("random").get(0).path("title").asText();

        return getArticle(title);
    }

    public Article getArticle(String title) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("action", "query");
        params.put("prop", "extracts|pageimages|info");
        params.put("inprop", "url");
        params.put("pithumbsize", "500");
        params.put("titles", title);
        params.put("format", "json");
        params.put("explaintext", "1");
        params.put("exintro", "0");
        params.put("exchars", "20000");

        StringBuilder urlBuilder = new StringBuilder(API_URL + "?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (urlBuilder.charAt(urlBuilder.length() - 1) != '?') {
                urlBuilder.append('&');
            }
            urlBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8))
                     .append('=')
                     .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder.toString()))
                .header("Accept", "application/json")
                .header("User-Agent", "OpenWiki/1.0")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode page = root.path("query").path("pages").elements().next();

        Article article = new Article();
        article.setTitle(page.path("title").asText());
        
        String content = page.path("extract").asText(null);
        if (content == null || content.isEmpty()) {
            content = "Contenuto non disponibile";
        }
        article.setContent(content);
        
        article.setPageId(page.path("pageid").asText());
        article.setWikiUrl(page.path("fullurl").asText());

        if (page.has("thumbnail")) {
            article.setImageUrl(page.path("thumbnail").path("source").asText());
        }

        logger.info("Created article with fields - imageUrl: {}, pageId: {}, wikiUrl: {}", 
                    article.getImageUrl(), article.getPageId(), article.getWikiUrl());

        return article;
    }
} 