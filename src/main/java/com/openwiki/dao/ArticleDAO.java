package com.openwiki.dao;

import com.openwiki.config.DatabaseConfig;
import com.openwiki.model.Article;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleDAO {
    private static final Logger logger = LoggerFactory.getLogger(ArticleDAO.class);

    private static final String INSERT_ARTICLE = 
        "INSERT INTO saved_articles (user_id, title, content, image_url, date_downloaded, page_id, wiki_url) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    public Article save(Article article) throws Exception {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_ARTICLE, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, Integer.parseInt(article.getUserId()));
            stmt.setString(2, article.getTitle());
            stmt.setString(3, article.getContent());
            stmt.setString(4, article.getImageUrl());
            stmt.setTimestamp(5, Timestamp.valueOf(article.getDateDownloaded()));
            stmt.setString(6, article.getPageId());
            stmt.setString(7, article.getWikiUrl());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating article failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(String.valueOf(generatedKeys.getLong(1)));
                }
            }

            return article;
        }
    }
    
    public Optional<Article> findById(String id) throws SQLException {
        String sql = "SELECT * FROM saved_articles WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToArticle(rs));
            }
            return Optional.empty();
        }
    }
    
    public List<Article> findByUserId(String userId) throws SQLException {
        String sql = "SELECT * FROM saved_articles WHERE user_id = ?";
        List<Article> articles = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, Integer.parseInt(userId));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                articles.add(mapResultSetToArticle(rs));
            }
            return articles;
        }
    }
    
    private Article mapResultSetToArticle(ResultSet rs) throws SQLException {
        Article article = new Article();
        article.setId(String.valueOf(rs.getInt("id")));
        article.setUserId(String.valueOf(rs.getInt("user_id")));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("content"));
        article.setImageUrl(rs.getString("image_url"));
        article.setDateDownloaded(rs.getTimestamp("date_downloaded").toLocalDateTime());
        article.setPageId(rs.getString("page_id"));
        article.setWikiUrl(rs.getString("wiki_url"));
        return article;
    }

    public boolean deleteArticle(String articleId, String userId) throws SQLException {
        String sql = "DELETE FROM saved_articles WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            int id = Integer.parseInt(articleId);
            int uid = Integer.parseInt(userId);
            
            logger.info("Executing DELETE query with id={} and user_id={}", id, uid);
            stmt.setInt(1, id);
            stmt.setInt(2, uid);
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Delete operation affected {} rows", rowsAffected);
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error deleting article: {}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean updateArticle(Article article) throws SQLException {
        String sql = "UPDATE saved_articles SET title = ?, content = ?, image_url = ?, wiki_url = ?, page_id = ? " +
                     "WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, article.getTitle());
            stmt.setString(2, article.getContent());
            stmt.setString(3, article.getImageUrl());
            stmt.setString(4, article.getWikiUrl());
            stmt.setString(5, article.getPageId());
            stmt.setInt(6, Integer.parseInt(article.getId()));
            stmt.setInt(7, Integer.parseInt(article.getUserId()));
            
            int rowsAffected = stmt.executeUpdate();
            logger.info("Update affected {} rows", rowsAffected);
            return rowsAffected > 0;
        }
    }
} 