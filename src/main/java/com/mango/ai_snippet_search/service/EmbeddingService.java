package com.mango.ai_snippet_search.service;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class EmbeddingService {
    private static final Dotenv dotenv = Dotenv.load();
    private static final String OPENAI_API_KEY = dotenv.get("OPENAI_API_KEY");
    private static final String EMBEDDING_ENDPOINT = "https://api.openai.com/v1/embeddings";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static float[] getEmbedding(String input) throws IOException {
        String jsonBody = "{ \"input\": \"" + input.replace("\"", "\\\"") + "\", \"model\": \"text-embedding-ada-002\" }";

        Request request = new Request.Builder()
                .url(EMBEDDING_ENDPOINT)
                .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Embedding API call failed: " + response);
        }

        String responseBody = response.body().string();
        var tree = mapper.readTree(responseBody);
        var embeddingArray = tree.get("data").get(0).get("embedding");

        float[] embedding = new float[embeddingArray.size()];
        for (int i = 0; i < embeddingArray.size(); i++) {
            embedding[i] = (float) embeddingArray.get(i).asDouble();
        }
        return embedding;
    }

    public double cosineSimilarity(float[] a, float[] b) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
