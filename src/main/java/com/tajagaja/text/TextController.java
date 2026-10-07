package com.tajagaja.text;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

// /api/texts: 글 목록 조회, 글 한 개 조회, 붙여넣은 글 추가
@RestController
@RequestMapping("/api/texts")
public class TextController {

    private final TextRepository repository;

    // 스프링이 저장소 객체를 자동으로 넣어줌 (의존성 주입)
    public TextController(TextRepository repository) {
        this.repository = repository;
    }

    // GET /api/texts : 전체 글 목록 (JSON 배열로 응답)
    @GetMapping
    public List<TypingText> list() {
        return repository.findAll();
    }

    // GET /api/texts/{id} : 글 한 개. 없으면 404
    @GetMapping("/{id}")
    public TypingText get(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "글이 없어요"));
    }

    // POST /api/texts : 붙여넣은 글 추가
    @PostMapping
    public TypingText add(@RequestBody AddTextRequest req) {
        if (req.content() == null || req.content().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용을 입력해 주세요");
        }
        // [상속 활용] 붙여넣은 글은 CustomText로 만든다. 공백 정리와 제목 기본값은 CustomText.of()가 처리한다.
        return repository.save(id -> CustomText.of(id, req.title(), req.content()));
    }

    // DELETE /api/texts/{id} : 내가 붙여넣은 글(CUSTOM)만 삭제. 없으면 404, 기본 글이면 403
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        TypingText text = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "글이 없어요"));
        if (!"CUSTOM".equals(text.getCategory())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "기본 글은 삭제할 수 없어요");
        }
        repository.deleteById(id);
    }

    public record AddTextRequest(String title, String content) {}
}
