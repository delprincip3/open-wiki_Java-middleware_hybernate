package com.openwiki.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WikiSearchResult {
    private String title;
    private String excerpt;
    private String pageId;
    private String url;
} 