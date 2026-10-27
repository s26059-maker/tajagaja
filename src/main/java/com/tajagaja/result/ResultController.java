package com.tajagaja.result;

import com.tajagaja.text.TextRepository;
import com.tajagaja.text.TypingText;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final TextRepository repository;

    public ResultController(TextRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResultDto.Response result(@RequestBody ResultDto.Request req) {
        TypingText text = repository.findById(req.textId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "글이 없어요"));
        String typed = req.typed() == null ? "" : req.typed();

        return new ResultDto.Response(
                text.accuracy(typed),
                text.cpm(typed, req.elapsedMs()),
                text.typoCount(typed)
        );
    }
}
