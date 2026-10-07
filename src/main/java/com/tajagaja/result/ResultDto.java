package com.tajagaja.result;

// 브라우저와 서버가 주고받는 채점 데이터의 모양
public class ResultDto {
    // 요청: 글 번호 / 사용자가 친 내용 / 걸린 시간(ms)
    public record Request(Long textId, String typed, long elapsedMs) {}
    // 응답: 정확도(%) / 분당 타수 / 오타 개수
    public record Response(int accuracy, int cpm, int typoCount) {}
}
