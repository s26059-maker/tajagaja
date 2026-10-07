package com.tajagaja.text;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

// DB 없이 메모리에 저장 (서버를 끄면 사라짐)
@Repository
public class TextRepository {

    // [상속 활용] 타입이 부모(TypingText)라서 SongText와 CustomText를 한 상자에 함께 담을 수 있다.
    private final Map<Long, TypingText> store = new ConcurrentHashMap<>();
    // 글 번호를 1, 2, 3... 순서로 발급 (동시 요청에도 안전)
    private final AtomicLong seq = new AtomicLong();

    // 서버가 켜질 때 기본 글(SongText) 2개를 미리 넣어둠
    public TextRepository() {
        save(id -> new SongText(id, "0 + 0","검은 눈동자의 사각지대를 찾으러 가자 여름 코코아, 겨울 수박도 혼나지 않는 파라다이스 앞서가는 너의 머리가 두 볼을 간지럽힐 때 나의 내일이 뛰어오네 난 널 버리지 않아 너도 같은 생각이지? 저 너머의 우리는 결코 우리가 될 수 없단다 영생과 영면의 차이를 너는 알고 있니? 멍든 발목을 꺾으려 해도 망설임 없이 태어나는 꿈 난 널 버리지 않아 너도 같은 생각이지? 저 너머의 우리는 결코 우리가 될 수 없단다 아, 난 널 버리지 않아 너도 같은 생각이지? 난 우리를 영영 잃지 않아 너도 영영 그럴 거지?"));
        save(id -> new SongText(id, "가시","너 없는 지금도 눈부신 하늘과 눈부시게 웃는 사람들 나의 헤어짐은 모르는 세상은 슬프도록 그대로인데 시간마저 데려가지 못하게 나만은 널 보내지 못했나 봐 가시처럼 깊게 박힌 기억은 아파도 아픈 줄 모르고 그대 기억이 지난 사랑이 내 안을 파고드는 가시가 되어 제발 가라고 아주 가라고 애써도 나를 괴롭히는데 너무 사랑했던 나를 크게 두려웠던 나를 미치도록 너를 그리워했던 날 이제는 놓아줘 보이지 않아 내 안에 숨어 잊으려 하면 할수록 더 아파와 제발 가라고 아주 가라고 애써도 나를 괴롭히는데."));
    }

    // 번호를 뽑아 factory에 넘기면 그 번호로 글(SongText 또는 CustomText)을 만들어 주고, 저장 후 반환한다.
    // [상속 활용] 반환 타입이 부모(TypingText)라서 어떤 자식이든 같은 메서드로 저장할 수 있다.
    public TypingText save(Function<Long, TypingText> factory) {
        long id = seq.incrementAndGet();
        TypingText text = factory.apply(id);
        store.put(id, text);
        return text;
    }

    // 저장된 글 전체를 번호 순으로 반환
    public List<TypingText> findAll() {
        return store.values().stream()
                .sorted(Comparator.comparing(TypingText::getId))
                .toList();
    }

    // 번호로 글 하나를 찾음 (없을 수 있어서 Optional)
    public Optional<TypingText> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
