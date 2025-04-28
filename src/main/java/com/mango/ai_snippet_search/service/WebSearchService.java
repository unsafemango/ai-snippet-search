package com.mango.ai_snippet_search.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mango.ai_snippet_search.model.SearchResult;
import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Service
public class WebSearchService {
    @Autowired
    private ScrapingService scrapingService;

    @Autowired
    private EmbeddingService embeddingService;

    private static final Dotenv dotenv = Dotenv.load();
    private static final String SERP_API_KEY = dotenv.get("SERP_API_KEY");
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public List<SearchResult> searchInternetWithReRanking(String query) throws Exception {
        String url = "https://serpapi.com/search.json?q=" +
                URLEncoder.encode(query + " site:stackoverflow.com OR site:geeksforgeeks.org", "UTF-8") +
                "&engine=google" +
                "&api_key=" + SERP_API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        String responseBody = response.body().string();
        JsonNode root = mapper.readTree(responseBody);
        JsonNode results = root.path("organic_results");

        float[] queryEmbedding = embeddingService.getEmbedding(query);

        List<SearchResult> allResults = new ArrayList<>();

        for (JsonNode result : results) {
            String title = result.path("title").asText();
            String link = result.path("link").asText();
            String snippet = result.path("snippet").asText("");

            List<String> codeBlocks = new ArrayList<>();
            try {
                codeBlocks = scrapingService.extractCodeSnippets(link);
            } catch (Exception e) {
                System.out.println("Failed to scrape " + link);
            }

            for (String code : codeBlocks) {
                if (code == null) continue;

                code = code.trim();
                if (code.isEmpty()) continue;

                if (code.length() < 10) continue; // Skip extremely short codes (noise)
                if (code.length() > 4000) continue; // Skip extremely large codes

                try {
                    float[] codeEmbedding = embeddingService.getEmbedding(code);
                    double score = embeddingService.cosineSimilarity(queryEmbedding, codeEmbedding);

                    SearchResult searchResult = new SearchResult(title, link, code);
                    searchResult.similarityScore = score;
                    allResults.add(searchResult);
                } catch (IOException e) {
                    System.out.println("Failed to embed code snippet: " + code.substring(0, Math.min(100, code.length())) + "...");
                }
            }

        }

        // Sort all snippets by similarity score
        allResults.sort((a, b) -> Double.compare(b.similarityScore, a.similarityScore));

        // Return top 5 or 10 best matches
        return allResults.subList(0, Math.min(10, allResults.size()));
    }
}