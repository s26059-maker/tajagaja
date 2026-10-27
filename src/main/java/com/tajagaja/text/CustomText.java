package com.tajagaja.text;

// 사용자가 추가한 글. TypingText를 상속받음
public class CustomText extends TypingText {

    private CustomText(Long id, String title, String content) {
        super(id, title, content);
    }

    // 생성자는 private, 이 팩토리로만 만들어 제목/공백 정리를 항상 거친다
    public static CustomText of(Long id, String title, String raw) {
        String t = (title == null || title.isBlank()) ? "내 글" : title;

        String c = raw.replaceAll("[\\s\\u00A0\\u3000]+", " ").strip();
        return new CustomText(id, t, c);
    }

    // 부모의 "SONG"을 재정의 (다형성). 삭제 가능 여부 판단에 쓰인다
    @Override
    public String getCategory() {
        return "CUSTOM";
    }
}
