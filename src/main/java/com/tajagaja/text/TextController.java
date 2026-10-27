package com.tajagaja.text;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/texts")
// 글 목록/조회/추가/삭제 API
public class TextController {

    private final TextRepository repository;

    public TextController(TextRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TypingText> list() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public TypingText get(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "글이 없어요"));
    }

    @PostMapping
    public TypingText add(@RequestBody AddTextRequest req) {
        if (req.content() == null || req.content().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용을 입력해 주세요");
        }
        return repository.save(id -> CustomText.of(id, req.title(), req.content()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        TypingText text = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "글이 없어요"));
        // 기본 글(SONG)은 삭제 불가
        if (!"CUSTOM".equals(text.getCategory())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "기본 글은 삭제할 수 없어요");
        }
        repository.deleteById(id);
    }

    public record AddTextRequest(String title, String content) {}
}
