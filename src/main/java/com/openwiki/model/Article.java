package com.openwiki.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime dateDownloaded;
    private String pageId;
    private String wikiUrl;
} 