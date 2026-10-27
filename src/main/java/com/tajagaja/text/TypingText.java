package com.tajagaja.text;

// 연습할 글 하나(id, 제목, 원문)와 채점 로직.
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

    // 정확도: 같은 위치에서 맞은 글자 수 / 원문 길이
    public int accuracy(String typed) {
        if (content.isEmpty()) return 0;
        return (int) Math.round(correctCount(typed) * 100.0 / content.length());
    }

    // 오타 수: 친 범위 안에서 원문과 다른 글자 수
    public int typoCount(String typed) {
        return Math.min(content.length(), typed.length()) - correctCount(typed);
    }

    // 분당 타수: 맞게 친 글자의 키 입력 수 합 / 걸린 시간
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

    // 한글은 받침 없으면 2타, 있으면 3타, 그 외 1타
    private int strokeOf(char c) {
        if (c >= 0xAC00 && c <= 0xD7A3) {
            int jong = (c - 0xAC00) % 28;
            return jong == 0 ? 2 : 3;
        }
        return 1;
    }
}
