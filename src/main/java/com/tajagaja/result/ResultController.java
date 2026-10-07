package com.tajagaja.result;

import com.tajagaja.text.TextRepository;
import com.tajagaja.text.TypingText;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// /api/results: 연습이 끝나면 서버에서 최종 채점
@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final TextRepository repository;

    public ResultController(TextRepository repository) {
        this.repository = repository;
    }

    // POST /api/results : 글 번호 + 사용자가 친 내용 + 걸린 시간을 받아 정확도/타수/오타 수를 돌려줌
    @PostMapping
    public ResultDto.Response result(@RequestBody ResultDto.Request req) {
        TypingText text = repository.findById(req.textId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "글이 없어요"));
        String typed = req.typed() == null ? "" : req.typed();

        // [상속 활용] text가 SongText인지 CustomText인지 구분하지 않는다.
        // 둘 다 부모(TypingText)의 채점 메서드를 물려받았기 때문에 같은 코드로 채점된다.
        return new ResultDto.Response(
                text.accuracy(typed),
                text.cpm(typed, req.elapsedMs()),
                text.typoCount(typed)
        );
    }
}
