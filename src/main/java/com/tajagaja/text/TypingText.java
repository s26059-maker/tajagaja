package com.tajagaja.text;

/*
 * [상속] 부모 클래스
 * - abstract: 이 클래스로 직접 new 할 수 없고, 자식(SongText, CustomText)을 통해서만 만들어진다.
 * - 모든 글이 똑같이 쓰는 것(id/제목/내용 필드, 채점 로직)을 여기에 한 번만 작성한다.
 *   → 자식은 코드를 다시 쓰지 않아도 그대로 물려받으므로, 기본 글과 붙여넣은 글이 항상 같은 방식으로 채점된다.
 * - 자식마다 달라야 하는 것(분류)은 abstract 메서드로 남겨서 자식이 반드시 구현하도록 강제한다.
 */
public abstract class TypingText {

    private final Long id;
    private final String title;
    private final String content;

    // protected: 자식 클래스의 생성자에서 super(...)로만 호출할 수 있다.
    protected TypingText(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // get 메서드 이름 덕분에 스프링이 JSON으로 바꿀 때 "id", "title", "content", "category" 필드가 만들어진다.
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }

    // [상속] 자식이 반드시 덮어써서(@Override) 구현해야 하는 메서드. 화면의 "기본"/"내 글" 뱃지 구분에 쓰인다.
    public abstract String getCategory();

    // ---- 공통 채점 로직: 자식이 그대로 물려받아 사용 ----

    // 정확도(%): 원문과 같은 위치의 글자가 맞은 비율
    public int accuracy(String typed) {
        if (content.isEmpty()) return 0;
        return (int) Math.round(correctCount(typed) * 100.0 / content.length());
    }

    // 오타 개수: 비교한 글자 수 - 맞은 글자 수
    public int typoCount(String typed) {
        return Math.min(content.length(), typed.length()) - correctCount(typed);
    }

    // 분당 타수: 맞게 친 글자를 자판 누른 횟수로 환산해 1분 기준으로 계산
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

    // 한글 한 글자 = 초성+중성(2) + 받침 있으면 1. 영어/숫자/공백은 1
    // TODO(2주차): 겹모음(ㅘ), 겹받침(ㄳ) 추가 처리
    private int strokeOf(char c) {
        if (c >= 0xAC00 && c <= 0xD7A3) {
            int jong = (c - 0xAC00) % 28;
            return jong == 0 ? 2 : 3;
        }
        return 1;
    }
}
