package hello.aop.order;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 스프링 AOP 구현 - 예제 프로젝트 만들기
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public String orderItem(String itemId) {
        log.info("[OrderService] 실행");
        return orderRepository.save(itemId);
    }
}
