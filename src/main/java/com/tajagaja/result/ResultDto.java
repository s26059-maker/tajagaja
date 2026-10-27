package com.tajagaja.result;

public class ResultDto {

    public record Request(Long textId, String typed, long elapsedMs) {

    }
    public record Response(int accuracy, int cpm, int typoCount) {}
}
