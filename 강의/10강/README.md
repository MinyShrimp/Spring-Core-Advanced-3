# 스프링 AOP 구현

## 예제 프로젝트 만들기

### 예제

#### OrderRepository

```java
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

```

#### OrderService

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void orderItem(String itemId) {
        log.info("[OrderService] 실행");
        orderRepository.save(itemId);
    }
}
```

#### AopTest

```java
@Slf4j
@SpringBootTest
public class AopTest {
    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void aopInfo() {
        log.info("isAopProxy, orderService = {}", AopUtils.isAopProxy(orderService));
        log.info("isAopProxy, orderRepository = {}", AopUtils.isAopProxy(orderRepository));
    }

    @Test
    void success() {
        orderService.orderItem("itemA");
    }

    @Test
    void exception() {
        Assertions.assertThatThrownBy(
                () -> orderService.orderItem("ex")
        ).isInstanceOf(IllegalStateException.class);
    }
}
```

### 실행 로그

#### aopInfo

```
isAopProxy, orderService = false
isAopProxy, orderRepository = false
```

#### success

```java
[OrderService] 실행
[OrderRepository] 실행
```

## 스프링 AOP 구현 1 - 시작

## 스프링 AOP 구현 2 - 포인트컷 분리

## 스프링 AOP 구현 3 - 어드바이스 추가

## 스프링 AOP 구현 4 - 포인트컷 참조

## 스프링 AOP 구현 5 - 어드바이스 순서

## 스프링 AOP 구현 6 - 어드바이스 종류