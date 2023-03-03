package hello.aop.order;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 스프링 AOP 구현 - 예제 프로젝트 만들기
 */
@Slf4j
@Repository
public class OrderRepository {

    public String save(String itemId) {
        log.info("[OrderRepository] 실행");

        if (itemId.equals("ex")) {
            throw new IllegalStateException("예외 발생!");
        }

        return itemId;
    }
}
