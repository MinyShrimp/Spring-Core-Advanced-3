# 스프링 AOP - 포인트컷

## 포인트컷 지시자

### 포인트컷 지시자

> @Pointcut("execution(* hello.aop.order..*(..))")

AspectJ는 포인트컷을 편리하게 표현하기 위한 특별한 표현식을 제공한다.

#### 종류

* `execution`
    * 메소드 실행 조인 포인트를 매칭한다.
    * 스프링 AOP에서 가장 많이 사용하고, 기능도 복잡하다.
* `within`
    * 특정 타입 내의 조인 포인트를 매칭한다.
* `args`
    * 인자가 주어진 타입의 인스턴스인 조인 포인트
* `this`
    * 스프링 빈 객체(스프링 AOP 프록시)를 대상으로 하는 조인 포인트
* `target`
    * Target 객체(스프링 AOP 프록시가 가르키는 실제 대상)를 대상으로 하는 조인 포인트
* `@target`
    * 실행 객체의 클래스에 주어진 타입의 애노테이션이 있는 조인 포인트
* `@within`
    * 주어진 애노테이션이 있는 타입 내 조인 포인트
* `@annotation`
    * 메서드가 주어진 애노테이션을 가지고 있는 조인 포인트를 매칭
* `@args`
    * 전달된 실제 인수의 런타임 타입이 주어진 타입의 애노테이션을 갖는 조인 포인트
* `bean`
    * 스프링 전용 포인트컷 지시자, 빈의 이름으로 포인트컷을 지정한다.

`execution`은 가장 많이 사용하고, 나머지는 자주 사용하지 않는다.
따라서 `execution`을 중점적으로 이해하자.

## 예제 만들기

### 예제

#### ClassAop

```java
/**
 * Class 단위 AOP 적용을 위해 생성
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassAop {
}
```

#### MethodAop

```java
/**
 * Method 단위 AOP 적용을 위해 생성
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodAop {

    String value();
}
```

#### MemberService

```java
/**
 * 상속받은 인터페이스의 정보를 얻기 위해 생성
 */
public interface MemberService {
    String hello(String param);
}
```

#### MemberServiceImpl

```java
@ClassAop
@Component
public class MemberServiceImpl implements MemberService {

    @Override
    @MethodAop("test value")
    public String hello(String param) {
        return "ok";
    }

    public String internal(String param) {
        return "ok";
    }
}
```

#### ExecutionTest

```java
/**
 * 포인트컷 지시자 - execution 테스트
 */
@Slf4j
public class ExecutionTest {

    final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    void printMethod() {
        // public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
        log.info("helloMethod = {}", helloMethod);
    }
}
```

* `AspectJExpressionPointcut`
    * 포인트컷 표현식을 처리해주는 클래스
    * 상위에 `Pointcut` 인터페이스를 가진다.

### 실행 결과

```
helloMethod = public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
```

## execution - 1

### 문법

```
[접근제어자]? [반환타입] [선언타입]?[메서드이름]([파라미터]) [예외]?
```

* 메서드 실행 JoinPoint를 매칭한다.
* `?`는 생략 가능하다.
* `*` 패턴을 사용할 수 있다.

### 예제

[ExecutionTest.java](../../src/test/java/hello/aop/pointcut/ExecutionTest.java)

### 가장 정확한 포인트컷

```java
@Test
void exactMatch() {
    pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
    assertThat(
            pointcut.matches(helloMethod, MemberServiceImpl.class)
    ).isTrue();
}
```

`MemberServiceImpl.hello(String)` 메서드와 가장 정확하게 매칭되는 표현식이다.

#### 매칭 조건

| 조건     | 내용                                 |
|--------|------------------------------------|
| 접근제어자  | public                             |
| 반환타입   | String                             |
| 선언타입   | hello.aop.member.MemberServiceImpl |
| 메서드 이름 | hello                              |
| 파라미터   | String                             |
| 예외     | 생략                                 |

### 가장 많이 생략한 포인트 컷

```
execution(* *(..))
```

#### 매칭 조건

| 조건     |   내용   |
|--------|:------:|
| 접근제어자  |   생략   |
| 반환타입   |  `*`   |
| 선언타입   |   생략   |
| 메서드 이름 |  `*`   |
| 파라미터   | `(..)` |
| 예외     |   생략   |

### 메서드 이름 매칭

```
// 이름이 "hello"와 정확하게 매칭
execution(* hello(..))

// 이름이 "hel"로 시작하면 매칭
execution(* hel*(..))

// 이름 중간에 "el"이 있으면 매칭
execution(* *el*(..))
```

### 패키지 매칭

```
// 패키지가 "hello.aop.member"이고 
// 타입이 "MemberServiceImpl"이며
// 메서드 이름이 "hello"인 것과 매칭
execution(* hello.aop.member.MemberServiceImpl.hello(..))

// 패키지가 "hello.aop.member"이고 해당 패키지의 모든 메서드 매칭 
// 하위 패키지는 포함되지 않는다.
execution(* hello.aop.member.*.*(..))

// 패키지가 "hello.aop"이고 해당 패키지의 모든 매서드 매칭
// 하위 패키지는 포함되지 않는다.
execution(* hello.aop.*.*(..))

// 패키지가 "hello.aop.member"이고 해당 패키지와 하위 패키지의 모든 메서드 매칭
execution(* hello.aop.member..*.*(..))

// 패키지가 "hello.aop"이고 해당 패키지와 하위 패키지의 모든 메서드 매칭
execution(* hello.aop..*.*(..))
```

* `hello.aop.member.*(1).*(2)`
    * `(1)`: 타입
    * `(2)`: 메서드 이름
* `.`, `..`
    * `.`: 정확하게 해당 위치의 패키지
    * `..`: 해당 위치의 패키지와 그 하위 패키지도 모두 포함

## execution - 2

### 예제

[ExecutionTest.java](../../src/test/java/hello/aop/pointcut/ExecutionTest.java)

### 타입 매칭

* 부모 타입으로 자식 타입 매칭이 된다.
* 자식 타입에만 있는 메서드는 부모 타입으로 매칭이 되지 않는다.

### 파라미터 매칭

* `(String)`
    * 정확하게 String 타입 파라미터
* `()`
    * 파라미터가 없어야 한다.
* `(*)`
    * 정확히 하나의 파라미터, 단 모든 타입을 허용한다.
* `(*, *)`
    * 정확히 두 개의 파라미터, 단 모든 타입을 허용한다.
* `(..)`
    * 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
    * 참고로 파라미터가 없어도 된다. `0..*` 로 이해하면 된다.
* `(String, ..)`
    * String 타입으로 시작해야 한다.
    * 숫자와 무관하게 모든 파라미터, 모든 타입을 허용한다.
    * 예) `(String)`, `(String, Xxx)`, `(String, Xxx, Xxx)` 허용

## within

## args

## @target, @within

## @annotation, @args

## bean

## 매개변수 전달

## this, target