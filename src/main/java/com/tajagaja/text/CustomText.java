package com.tajagaja.text;

public class CustomText extends TypingText {

    private CustomText(Long id, String title, String content) {
        super(id, title, content);
    }

    public static CustomText of(Long id, String title, String raw) {
        String t = (title == null || title.isBlank()) ? "내 글" : title;

        String c = raw.replaceAll("[\\s\\u00A0\\u3000]+", " ").strip();
        return new CustomText(id, t, c);
    }

    @Override
    public String getCategory() {
        return "CUSTOM";
    }
}
