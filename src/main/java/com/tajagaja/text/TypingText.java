package com.tajagaja.text;

public class TypingText {

    private final Long id;
    private final String title;
    private final String content;

    protected TypingText(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getCategory() { return "SONG"; }

    public int accuracy(String typed) {
        if (content.isEmpty()) return 0;
        return (int) Math.round(correctCount(typed) * 100.0 / content.length());
    }

    public int typoCount(String typed) {
        return Math.min(content.length(), typed.length()) - correctCount(typed);
    }

    public int cpm(String typed, long elapsedMs) {
        if (elapsedMs <= 0) return 0;
        int strokes = 0;
        for (int i = 0; i < Math.min(content.length(), typed.length()); i++) {
            if (content.charAt(i) == typed.charAt(i)) strokes += strokeOf(typed.charAt(i));
        }
        return (int) Math.round(strokes * 60000.0 / elapsedMs);
    }

    private int correctCount(String typed) {
        int correct = 0;
        for (int i = 0; i < Math.min(content.length(), typed.length()); i++) {
            if (content.charAt(i) == typed.charAt(i)) correct++;
        }
        return correct;
    }

    private int strokeOf(char c) {
        if (c >= 0xAC00 && c <= 0xD7A3) {
            int jong = (c - 0xAC00) % 28;
            return jong == 0 ? 2 : 3;
        }
        return 1;
    }
}
