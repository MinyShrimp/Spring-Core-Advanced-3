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

### 예제

#### AspectV1

```java
@Slf4j
@Aspect
public class AspectV1 {

    @Around("execution(* hello.aop.order..*(..))")
    public Object doLog(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }
}
```

* `@Around` 애노테이션의 값인 `execution(* hello.aop.order..*(..))` 는 포인트컷이 된다.
* `@Around` 애노테이션의 메서드인 `doLog` 는 어드바이스(`Advice`)가 된다.
* `execution(* hello.aop.order..*(..))`
    * `hello.aop.order` 패키지와 그 하위 패키지(`..`)를 지정하는 `AspectJ` 포인트컷 표현식이다.
    * 앞으로는 간단히 포인트컷 표현식이라 하겠다.
    * 참고로 표인트컷 표현식은 뒤에서 자세히 설명하겠다.
* 이제 `OrderService`, `OrderRepository`의 모든 메서드는 AOP 적용의 대상이 된다.
    * 참고로 스프링은 프록시 방식의 AOP를 사용하므로 프록시를 통하는 메서드만 적용 대상이 된다.

> **참고**
>
> 스프링 AOP는 AspectJ의 문법을 차용하고, 프록시 방식의 AOP를 제공한다.
> AspectJ를 직접 사용하는 것이 아니다.
>
> 스프링 AOP를 사용할 때는 `@Aspect` 애노테이션을 주로 사용하는데,
> 이 애노테이션도 AspectJ가 제공하는 애노테이션이다.

> **참고**
>
> `@Aspect`를 포함한 `org.aspectj` 패키지 관련 기능은 `aspectjweaver.jar` 라이브러리가 제공하는 기능이다.
>
> 앞서 `build.gradle`에 `spring-boot-starter-aop`를 포함했는데,
> 이렇게 하면 스프링의 AOP 관련 기능과 함께 `aspectjweaver.jar`도 함께 사용할 수 있게 의존 관계에 포함된다.
>
> 그런데 스프링에서는 AspectJ가 제공하는 애노테이션이나 관련 인터페이스만 사용하는 것이고,
> 실제 AspectJ가 제공하는 컴파일, 로드타임 위버 등을 사용하는 것은 아니다.
> 스프링은 지금까지 우리가 학습한 것 처럼 프록시 방식의 AOP를 사용한다.

#### AopTest

```java
@Slf4j
@Import({AspectV1.class}) // 추가
@SpringBootTest
public class AopTest { ... }
```

`@Aspect`는 애스펙트라는 표식이지 컴포넌트 스캔이 되는 것은 아니다.
따라서 `AspectV1`를 AOP로 사용하려면 스프링 빈으로 등록해야 한다.

스프링 빈으로 등록하는 방법은 다음과 같다.

* `@Bean`을 사용해서 직접 등록
* `@Component` 컴포넌트 스캔을 사용해서 자동 등록
* `@Import` 주로 설정 파일을 추가할 때 사용(`@Configuration`)

### 실행 로그

#### aopInfo

```
isAopProxy, orderService = true
isAopProxy, orderRepository = true
```

#### success

```
[log] void hello.aop.order.OrderService.orderItem(String)
[OrderService] 실행
[log] String hello.aop.order.OrderRepository.save(String)
[OrderRepository] 실행
```

## 스프링 AOP 구현 2 - 포인트컷 분리

### 포인트컷 분리

#### AspectV2

```java
@Slf4j
@Aspect
public class AspectV2 {

    /**
     * 포인트컷 분리
     * - 포인트컷 시그니처: 메서드 이름 + 파라미터
     * - 반환 타입: void
     * - 코드 내용은 비워둔다.
     * - 하나의 포인트컷 표현식을 여러 어드바이스에서 함께 사용할 수 있다.
     * - public으로 선언하면 다른 클래스의 외부 어드바이스에서도 함께 사용할 수 있다.
     *
     * @see Pointcut
     */
    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder() {
    }

    /**
     * 분리한 포인트컷을 아래와 같이 사용
     */
    @Around("allOrder()")
    public Object doLog(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }
}
```

#### @Pointcut

* 포인트컷 시그니처: 메서드 이름 + 파라미터
* 반환 타입: `void`
* 코드 내용은 비워둔다.
* 하나의 포인트컷 표현식을 여러 어드바이스에서 함께 사용할 수 있다.
* `public`으로 선언하면 다른 클래스의 외부 어드바이스에서도 함께 사용할 수 있다.

## 스프링 AOP 구현 3 - 어드바이스 추가

### 어드바이스 추가

앞서 로그를 출력하는 기능에 추가로 트랜잭션을 적용하는 코드도 추가해보자.
여기서는 진짜 트랜잭션을 실행하는 것은 아니다. 기능이 동작한 것 처럼 로그만 남기겠다.

#### 트랜잭션 기능은 보통 다음과 같이 동작한다.

* 핵심 로직 실행 직전에 트랜잭션을 시작
* 핵심 로직 실행
* 핵심 로직 실행에 문제가 없으면 커밋
* 핵심 로직 실행에 예외가 발생하면 롤백

### 예제

#### AspectV3

```java
@Slf4j
@Aspect
public class AspectV3 {

    /**
     * hello.aop.order 패키지와 하위 패키지
     */
    @Pointcut("execution(* hello.aop.order..*(..))")
    private void allOrder() {
    }

    /**
     * 타입 이름 패턴이 *Service
     */
    @Pointcut("execution(* *..*Service.*(..))")
    private void allService() {
    }

    /**
     * hello.aop.order 패키지와 하위 패키지
     */
    @Around("allOrder()")
    public Object doLog(
            ProceedingJoinPoint joinPoint
    ) throws Throwable { ... }

    /**
     * hello.aop.order 패키지와 하위 패키지 +
     * 타입 이름 패턴이 *Service
     */
    @Around("allOrder() && allService()")
    public Object doTransaction(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {
        Signature signature = joinPoint.getSignature();

        try {
            log.info("[트랜잭션 시작] {}", signature);

            Object result = joinPoint.proceed();

            log.info("[트랜잭션 커밋] {}", signature);
            return result;
        } catch (Exception e) {
            log.info("[트랜잭션 롤백] {}", signature);
            throw e;
        } finally {
            log.info("[Resource Release] {}", signature);
        }
    }
}
```

#### AopTest

```java
@Slf4j
@Import({AspectV3.class}) // 교체
@SpringBootTest
public class AopTest { ... }
```

#### AOP 적용 결과

* `orderService`
    * `doLog()`, `doTransaction()` 어드바이스 적용
* `orderRepository`
    * `doLog()` 어드바이스 적용

### 실행 로그

![img.png](img.png)

#### success

```
# Client -> doLog -> doTransaction -> OrderService
[log] void hello.aop.order.OrderService.orderItem(String)
[트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[OrderService] 실행

# -> doLog -> OrderRepository
[log] String hello.aop.order.OrderRepository.save(String)
[OrderRepository] 실행

# OrderRepository -> doLog 
# -> OrderService -> doTrasaction -> doLog -> Client
[트랜잭션 커밋] void hello.aop.order.OrderService.orderItem(String)
[Resource Release] void hello.aop.order.OrderService.orderItem(String)
```

#### exception

```
# Client -> doLog -> doTransaction -> OrderService
[log] void hello.aop.order.OrderService.orderItem(String)
[트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)

# -> doLog -> OrderRepository
[OrderService] 실행
[log] String hello.aop.order.OrderRepository.save(String)
[OrderRepository] 실행

# OrderRepository -> doLog 
# -> OrderService -> doTrasaction -> doLog -> Client
[트랜잭션 롤백] void hello.aop.order.OrderService.orderItem(String)
[Resource Release] void hello.aop.order.OrderService.orderItem(String)
```

## 스프링 AOP 구현 4 - 포인트컷 참조

### 포인트컷 파일 분리

* 포인트컷을 공용으로 사용하기 위해 별도의 외부 클래스에 모아두어도 된다.
* 참고로 외부에서 호출할 때는 포인트컷의 접근 제어자를 `public`으로 열어두어야 한다.

### 예제

#### Pointcuts

```java
/**
 * 포인트컷을 외부에 뺄 수 있다.
 */
public class Pointcuts {

    /**
     * hello.aop.order 패키지와 하위 패키지
     */
    @Pointcut("execution(* hello.aop.order..*(..))")
    public void allOrder() {
    }

    /**
     * 타입 패턴이 *Service
     */
    @Pointcut("execution(* *..*Service.*(..))")
    public void allService() {
    }

    /**
     * allOrder && allService
     */
    @Pointcut("allOrder() && allService()")
    public void orderAndService() {
    }
}
```

#### AspectV4

```java
@Slf4j
@Aspect
public class AspectV4 {

    /**
     * 외부 클래스의 포인트컷을 가져오려면 풀 패키지로 작성해야한다.
     */
    @Around("hello.aop.order.aop.Pointcuts.allOrder()")
    public Object doLog(
            ProceedingJoinPoint joinPoint
    ) throws Throwable { ... }

    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(
            ProceedingJoinPoint joinPoint
    ) throws Throwable { ... }
}
```

## 스프링 AOP 구현 5 - 어드바이스 순서

### 어드바이스 순서

어드바이스는 기본적으로 **순서를 보장하지 않는다.**
순서를 지정하고 싶으면 `@Aspect` 적용 단위로 `org.springframework.core.annotation.@Order` 애노테이션을 적용해야 한다.

문제는 이것을 어드바이스 단위가 아니라 **클래스 단위로 적용할 수 있다는 점**이다.
그래서 지금처럼 하나의 애스펙트에 여러 어드바이스가 있으면 순서를 보장 받을 수 없다.
따라서 애스펙트를 **별도의 클래스로 분리**해야 한다.

### 예제

#### AspectV5

```java
/**
 * Order는 메서드 단위가 아닌 클래스 단위에서만 적용된다.
 * 때문에, 아래와 같이 각각 클래스를 새로 만들어 어드바이저로 만들어주어야 한다.
 */
@Slf4j
public class AspectV5 {

    // 2 순위
    @Aspect
    @Order(2)
    public static class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(
                ProceedingJoinPoint joinPoint
        ) throws Throwable { ... }
    }

    // 1 순위
    @Aspect
    @Order(1)
    public static class TxAspect {
        @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
        public Object doTransaction(
                ProceedingJoinPoint joinPoint
        ) throws Throwable { ... }
    }
}
```

#### AopTest

```java
@Slf4j
@Import({AspectV5.LogAspect.class, AspectV5.TxAspect.class})
@SpringBootTest
public class AopTest { ... }
```

### 실행 로그

![img_1.png](img_1.png)

#### success

```
# Client -> doTransaction -> doLog -> OrderService
[트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[log] void hello.aop.order.OrderService.orderItem(String)
[OrderService] 실행

# -> doLog -> OrderRepository
[log] String hello.aop.order.OrderRepository.save(String)
[OrderRepository] 실행

# OrderRepository -> doLog 
# -> OrderService -> doLog -> doTransaction -> Client
[트랜잭션 커밋] void hello.aop.order.OrderService.orderItem(String)
[Resource Release] void hello.aop.order.OrderService.orderItem(String)
```

#### exception

```
# Client -> doTransaction -> doLog -> OrderService
[트랜잭션 시작] void hello.aop.order.OrderService.orderItem(String)
[log] void hello.aop.order.OrderService.orderItem(String)
[OrderService] 실행

# -> doLog -> OrderRepository
[log] String hello.aop.order.OrderRepository.save(String)
[OrderRepository] 실행

# OrderRepository -> doLog 
# -> OrderService -> doLog -> doTransaction -> Client
[트랜잭션 롤백] void hello.aop.order.OrderService.orderItem(String)
[Resource Release] void hello.aop.order.OrderService.orderItem(String)
```

## 스프링 AOP 구현 6 - 어드바이스 종류

### 어드바이스 종류

* `@Around`
    * 메서드 호출 전후에 수행, 가장 강력한 어드바이스.
    * 조인 포인트 실행 여부 선택, 반환 값 변환, 예외 변환 등이 가능
* `@Before`
    * 조인 포인트 실행 이전에 실행
* `@AfterReturning`
    * 조인 포인트가 정상 완료후 실행
* `@AfterThrowing`
    * 메서드가 예외를 던지는 경우 실행
* `@After`
    * 조인 포인트가 정상 또는 예외에 관계없이 실행
    * `finally`

### 예제

#### AspectV6

```java
@Slf4j
@Aspect
public class AspectV6 {
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {
        Signature signature = joinPoint.getSignature();

        try {
            // Before
            log.info("[트랜잭션 시작] {}", signature);

            Object result = joinPoint.proceed();

            // AfterReturning
            log.info("[트랜잭션 커밋] {}", signature);
            return result;
        } catch (Exception e) {
            // AfterThrowing
            log.info("[트랜잭션 롤백] {}", signature);
            throw e;
        } finally {
            // After
            log.info("[Resource Release] {}", signature);
        }
    }
}
```

### JoinPoint

* 모든 어드바이스는 `org.aspectj.lang.JoinPoint`를 첫번째 파라미터에 사용할 수 있다. (생략해도 된다.)
* 단 `@Around`는 `ProceedingJoinPoint`을 사용해야 한다.
* 참고로 `ProceedingJoinPoint`는 `JoinPoint`의 하위 타입이다

#### JoinPoint 인터페이스의 주요 기능

* `getArgs()`
    * 메서드 인수를 반환합니다.
* `getThis()`
    * 프록시 객체를 반환합니다.
* `getTarget()`
    * 대상 객체를 반환합니다.
* `getSignature()`
    * 조언되는 메서드에 대한 설명을 반환합니다.
* `toString()`
    * 조언되는 방법에 대한 유용한 설명을 인쇄합니다.

#### ProceedingJoinPoint 인터페이스의 주요 기능

* `proceed()`: 다음 어드바이스나 타켓을 호출한다.

### 어드바이스 종류

![img_2.png](img_2.png)

#### Before

```java
/**
 * JoinPoint 호출 이전
 */
@Before("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doBefore(
        JoinPoint joinPoint
) {
    log.info("[before] {}", joinPoint.getSignature());
}
```

* `@Around`와 다르게 작업 흐름을 변경할 수는 없다.

* `@Around`는 `ProceedingJoinPoint.proceed()`를 호출해야 다음 대상이 호출된다.
    * 만약 호출하지 않으면 다음 대상이 호출되지 않는다.
* 반면에 `@Before`는 `ProceedingJoinPoint.proceed()` 자체를 사용하지 않는다.
    * 메서드 종료시 자동으로 다음 타켓이 호출된다.
* 물론 예외가 발생하면 다음 코드가 호출되지는 않는다.

#### AfterReturning

```java
/**
 * JoinPoint 정상 호출 이후
 */
@AfterReturning(
        value = "hello.aop.order.aop.Pointcuts.orderAndService()",
        returning = "result"
)
public void doAfterReturning(
        JoinPoint joinPoint, Object result
) {
    log.info("[return] {} return = {}", joinPoint.getSignature(), result);
}
```

* `returning` 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
* `returning` 절에 지정된 타입의 값을 반환하는 메서드만 대상으로 실행한다.
    * 부모 타입을 지정하면 모든 자식 타입은 인정된다.
* `@Around`와 다르게 반환되는 객체를 변경할 수는 없다.
    * 반환 객체를 변경하려면 `@Around`를 사용해야 한다.
    * 참고로 반환 객체를 조작할 수 는 있다.

#### AfterThrowing

```java
/**
 * JoinPoint 예외 발생 이후
 */
@AfterThrowing(
        value = "hello.aop.order.aop.Pointcuts.orderAndService()",
        throwing = "ex"
)
public void doAfterThrowing(
        JoinPoint joinPoint, Exception ex
) {
    log.info("[throw] {} message = {}", joinPoint.getSignature(), ex.getMessage());
}
```

* `throwing` 속성에 사용된 이름은 어드바이스 메서드의 매개변수 이름과 일치해야 한다.
* `throwing` 절에 지정된 타입과 맞는 예외를 대상으로 실행한다.
    * 부모 타입을 지정하면 모든 자식 타입은 인정된다.

#### After

```java
/**
 * JoinPoint 실행 이후 ( 정상이던 아니던 )
 */
@After("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doAfter(
        JoinPoint joinPoint
) {
    log.info("[after] {}", joinPoint.getSignature());
}
```

* 메서드 실행이 종료되면 실행된다. (`finally`를 생각하면 된다.)
* 정상 및 예외 반환 조건을 모두 처리한다.
* 일반적으로 리소스를 해제하는 데 사용한다.

#### Around

* 메서드의 실행의 주변에서 실행된다. 메서드 실행 전후에 작업을 수행한다.
* 가장 강력한 어드바이스
    * 조인 포인트 실행 여부 선택
        * `joinPoint.proceed()` 호출 여부 선택
    * 전달 값 변환
        * `joinPoint.proceed(args[])`
    * 반환 값 변환
    * 예외 변환
    * 트랜잭션 처럼 `try ~ catch ~ finally` 모두 들어가는 구문 처리 가능
* 어드바이스의 첫 번째 파라미터는 `ProceedingJoinPoint`를 사용해야 한다.
* `proceed()`를 통해 대상을 실행한다.
    * `proceed()`를 **여러번 실행할 수도 있음**(재시도)

### 실행

![img_2.png](img_2.png)

스프링은 5.2.7 버전부터 동일한 `@Aspect` 안에서 동일한 조인포인트의 우선순위를 정했다.

> 실행 순서: `@Around`, `@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`

어드바이스가 적용되는 순서는 이렇게 적용되지만, 호출 순서와 리턴 순서는 반대라는 점을 알아두자.
물론 `@Aspect` 안에 동일한 종류의 어드바이스가 2개 있으면 순서가 보장되지 않는다.
이 경우 앞서 배운 것 처럼 `@Aspect`를 분리하고 `@Order`를 적용하자.

#### success

```
[before] void hello.aop.order.OrderService.orderItem(String)
[OrderService] 실행
[OrderRepository] 실행
[return] void hello.aop.order.OrderService.orderItem(String) return = itemA
[after] void hello.aop.order.OrderService.orderItem(String)
```

#### exception

```
[before] void hello.aop.order.OrderService.orderItem(String)
[OrderService] 실행
[OrderRepository] 실행
[throw] void hello.aop.order.OrderService.orderItem(String) message = 예외 발생!
[after] void hello.aop.order.OrderService.orderItem(String)
```

### `@Around` 외에 다른 어드바이스가 존재하는 이유

`@Around` 하나만 있어도 모든 기능을 수행할 수 있다. 그런데 다른 어드바이스들이 존재하는 이유는 무엇일까?

#### Around, Before

```java
/**
 * joinPoint.proceed() 를 호출하지 않았다.
 * 다음 JoinPoint를 진행할 수 없어서 그래도 멈춰버린다.
 */
@Around("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doBefore(ProceedingJoinPoint joinPoint) {
    log.info("[before] {}", joinPoint.getSignature());
}

/**
 * joinPoint.proceed() 를 호출할 필요가 없다.
 * 즉, 진행이 멈출 위험이 사라진다.
 */
@Before("hello.aop.order.aop.Pointcuts.orderAndService()")
public void doBefore(JoinPoint joinPoint) {
    log.info("[before] {}", joinPoint.getSignature());
}
```

`@Around`가 가장 넓은 기능을 제공하는 것은 맞지만, 실수할 가능성이 있다.
반면에 `@Before`, `@After` 같은 어드바이스는 기능은 적지만 실수할 가능성이 낮고, 코드도 단순하다.

그리고 가장 중요한 점이 있는데, 바로 이 코드를 **작성한 의도가 명확**하게 드러난다는 점이다.
`@Before` 라는 애노테이션을 보는 순간 "아~ 이 코드는 타켓 실행 전에 한정해서 어떤 일을 하는 코드구나" 라는 것이 드러난다.

#### 좋은 설계는 제약이 있는 것이다

좋은 설계는 제약이 있는 것이다.
`@Around`만 있으면 되는데 왜? 이렇게 제약을 두는가?
제약은 실수를 미연에 방지한다. 일종의 가이드 역할을 한다.

만약 `@Around`를 사용했는데, 중간에 다른 개발자가 해당 코드를 수정해서 호출하지 않았다면?
큰 장애가 발생했을 것이다. 처음부터 `@Before`를 사용했다면 이런 문제 자체가 발생하지 않는다.

제약 덕분에 역할이 명확해진다.
다른 개발자도 이 코드를 보고 고민해야 하는 범위가 줄어들고 코드의 의도도 파악하기 쉽다.