package com.openwiki.dao;

import com.openwiki.config.HibernateConfig;
import com.openwiki.model.Article;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

public class ArticleDAO {
    private static final Logger logger = LoggerFactory.getLogger(ArticleDAO.class);

    public Article save(Article article) throws Exception {
        EntityManager em = HibernateConfig.getEntityManager();
        logger.info("Saving article with title: {}", article.getTitle());
        try {
            em.getTransaction().begin();
            em.persist(article);
            em.getTransaction().commit();
            logger.info("Article saved successfully with ID: {}", article.getId());
            return article;
        } catch (Exception e) {
            logger.error("Error saving article: {}", e.getMessage(), e);
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
    
    public Optional<Article> findById(String id) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            Article article = em.find(Article.class, id);
            return Optional.ofNullable(article);
        } finally {
            em.close();
        }
    }
    
    public List<Article> findByUserId(String userId) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            TypedQuery<Article> query = em.createQuery(
                "SELECT a FROM Article a WHERE a.userId = :userId", Article.class);
            query.setParameter("userId", userId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean deleteArticle(String articleId, String userId) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            int deleted = em.createQuery(
                "DELETE FROM Article a WHERE a.id = :id AND a.userId = :userId")
                .setParameter("id", articleId)
                .setParameter("userId", userId)
                .executeUpdate();
            em.getTransaction().commit();
            return deleted > 0;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public boolean updateArticle(Article article) {
        EntityManager em = HibernateConfig.getEntityManager();
        try {
            em.getTransaction().begin();
            Article existingArticle = em.find(Article.class, article.getId());
            if (existingArticle != null && existingArticle.getUserId().equals(article.getUserId())) {
                em.merge(article);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
} 