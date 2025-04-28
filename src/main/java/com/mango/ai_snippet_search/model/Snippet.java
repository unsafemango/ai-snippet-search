package com.mango.ai_snippet_search.model;

public class Snippet {
    public int id;
    public String title;
    public String description;
    public String code;
    public float[] embedding;

    public Snippet(int id, String title, String description, String code) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.code = code;
    }
}
