package com.mango.ai_snippet_search.service;

import com.mango.ai_snippet_search.model.Snippet;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocalSearchService {
    private List<Snippet> snippets = new ArrayList<>();

    public LocalSearchService() {
        snippets.add(new Snippet(1, "Open a File", "How to open a file in Java.", "BufferedReader reader = new BufferedReader(new FileReader(\"file.txt\"));"));
        snippets.add(new Snippet(2, "Write to a File", "How to write to a file in Java.", "BufferedWriter writer = new BufferedWriter(new FileWriter(\"file.txt\"));"));
    }

    public List<Snippet> search(String query) {
        // Basic naive search (title or description contains query)
        return snippets.stream()
                .filter(s -> s.title.toLowerCase().contains(query.toLowerCase()) ||
                        s.description.toLowerCase().contains(query.toLowerCase()))
                .limit(5)
                .collect(Collectors.toList());
    }
}
