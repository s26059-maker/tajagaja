package com.tajagaja.result;

// 채점 요청/응답 DTO 모음 (record)
public class ResultDto {

    public record Request(Long textId, String typed, long elapsedMs) {

    }
    public record Response(int accuracy, int cpm, int typoCount) {}
}
