package com.openwiki.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "saved_articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "title")
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "date_downloaded")
    private LocalDateTime dateDownloaded;
    
    @Column(name = "page_id")
    private String pageId;
    
    @Column(name = "wiki_url")
    private String wikiUrl;
} 