package com.mango.ai_snippet_search.controller;

import com.mango.ai_snippet_search.model.SearchResult;
import com.mango.ai_snippet_search.model.Snippet;
import com.mango.ai_snippet_search.service.EmbeddingService;
import com.mango.ai_snippet_search.service.LocalSearchService;
import com.mango.ai_snippet_search.service.WebSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    @Autowired
    private LocalSearchService localSearchService;

    @Autowired
    private WebSearchService webSearchService;

    @Autowired
    private EmbeddingService embeddingService;

    @GetMapping("/ping")
    public String ping() {
        return "Server is alive!";
    }

    @GetMapping("/search/local")
    public List<Snippet> localSearch(@RequestParam String q) {
        return localSearchService.search(q);
    }

    @GetMapping("/search/web")
    public List<SearchResult> webSearch(@RequestParam String q) throws Exception {
        return webSearchService.searchInternetWithReRanking(q);
    }

    @GetMapping("/test")
    public String testEmbedding() throws Exception {

        float[] embedding = embeddingService.getEmbedding("hello world");

        // Just return the first 5 numbers as a string
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.min(5, embedding.length); i++) {
            result.append(embedding[i]).append(", ");
        }
        return "First 5 values of embedding: [" + result.toString() + "...]";
    }

}
