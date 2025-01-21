package com.openwiki.service;

import com.openwiki.model.Article;
import com.openwiki.model.WikiSearchResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class WikiServiceTest {
    private WikiService wikiService;

    @BeforeEach
    void setUp() {
        wikiService = new WikiService();
    }

    @Test
    void search_withValidQuery_returnsResults() throws Exception {
        String query = "Roma";
        int limit = 5;
        
        List<WikiSearchResult> results = wikiService.search(query, limit);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertTrue(results.size() <= limit);
        
        WikiSearchResult firstResult = results.get(0);
        assertNotNull(firstResult.getTitle());
        assertNotNull(firstResult.getPageId());
        assertNotNull(firstResult.getUrl());
        assertNotNull(firstResult.getExcerpt());
    }

    @Test
    void getArticle_withValidTitle_returnsArticle() throws Exception {
        String title = "Roma";
        
        Article article = wikiService.getArticle(title);
        
        assertNotNull(article);
        assertEquals(title, article.getTitle());
        assertNotNull(article.getContent());
        assertNotNull(article.getPageId());
        assertNotNull(article.getWikiUrl());
    }

    @Test
    void getFeaturedArticle_returnsRandomArticle() throws Exception {
        Article article = wikiService.getFeaturedArticle();
        
        assertNotNull(article);
        assertNotNull(article.getTitle());
        assertNotNull(article.getContent());
        assertNotNull(article.getPageId());
        assertNotNull(article.getWikiUrl());
    }
} 