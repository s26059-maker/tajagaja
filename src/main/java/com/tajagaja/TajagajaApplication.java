package com.tajagaja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 스프링 부트 시작점. 이 패키지 아래의 @RestController, @Repository 등을 자동으로 찾아 등록한다.
@SpringBootApplication
public class TajagajaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TajagajaApplication.class, args);
    }
}
