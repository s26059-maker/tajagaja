package com.tajagaja.text;

/*
 * [상속] 자식 클래스 1: 기본 제공 글
 * - extends TypingText: 필드와 채점 로직(accuracy, typoCount, cpm)을 부모에게서 그대로 물려받는다.
 * - 이 클래스가 직접 하는 일은 분류 이름을 "SONG"으로 정하는 것뿐이다.
 */
public class SongText extends TypingText {

    // super(...): 부모 생성자를 호출해서 공통 필드를 채운다.
    public SongText(Long id, String title, String content) {
        super(id, title, content);
    }

    // [상속] 부모가 구현하라고 강제한 abstract 메서드를 덮어써서 구현
    @Override
    public String getCategory() {
        return "SONG";
    }
}
