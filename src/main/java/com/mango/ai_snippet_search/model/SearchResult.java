package com.mango.ai_snippet_search.model;

import java.util.List;

public class SearchResult {
    public String title;
    public String link;
    public String snippet;
    public List<String> codeSnippets;
    public double similarityScore;

    public SearchResult(String title, String link, String snippet) {
        this.title = title;
        this.link = link;
        this.snippet = snippet;
    }
}
