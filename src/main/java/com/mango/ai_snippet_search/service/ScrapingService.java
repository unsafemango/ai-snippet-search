package com.mango.ai_snippet_search.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScrapingService {

    public static List<String> extractCodeSnippets(String url) throws IOException {
        List<String> codeSnippets = new ArrayList<>();

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/117.0.0.0 Safari/537.36")
                .timeout(10000)  // 10 seconds timeout
                .get();

        Elements codeBlocks = doc.select("pre > code");

        for (Element codeBlock : codeBlocks) {
            codeSnippets.add(codeBlock.text());
        }

        return codeSnippets;
    }
}