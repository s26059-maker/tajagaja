package com.tajagaja.text;

/*
 * [상속] 자식 클래스 2: 사용자가 붙여넣은 글
 * - 채점 로직은 SongText와 똑같이 부모(TypingText)에서 물려받는다. → 붙여넣은 글에도 동일한 채점이 적용되는 이유
 * - 부모와 다른 점만 이 클래스에 둔다: 분류 "CUSTOM", 붙여넣은 글 전용 정리 규칙(공백 정리, 제목 기본값)
 */
public class CustomText extends TypingText {

    // private: 정리 규칙을 거치지 않고 만들 수 없도록, 반드시 아래 of()로만 생성하게 한다.
    private CustomText(Long id, String title, String content) {
        super(id, title, content);
    }

    // 붙여넣은 원본을 정리한 뒤 CustomText를 만드는 정적 팩토리 메서드
    public static CustomText of(Long id, String title, String raw) {
        String t = (title == null || title.isBlank()) ? "내 글" : title;
        // 줄바꿈·특수 공백·연속 공백을 스페이스 하나로 (안 그러면 띄어쓰기가 오타 처리됨)
        String c = raw.replaceAll("[\\s\\u00A0\\u3000]+", " ").strip();
        return new CustomText(id, t, c);
    }

    @Override
    public String getCategory() {
        return "CUSTOM";
    }
}
