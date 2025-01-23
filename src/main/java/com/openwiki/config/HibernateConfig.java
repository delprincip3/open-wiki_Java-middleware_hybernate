package com.openwiki.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class HibernateConfig {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("openwikiPU");
    
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public static void closeEntityManagerFactory() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
} 