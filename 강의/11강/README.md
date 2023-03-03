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

    static final AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    
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

## execution - 2

## within

## args

## @target, @within

## @annotation, @args

## bean

## 매개변수 전달

## this, target