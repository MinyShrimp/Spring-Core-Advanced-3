package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

/**
 * 스프링 AOP 구현 5 - 어드바이스 순서
 * <p>
 * Order는 메서드 단위가 아닌 클래스 단위에서만 적용된다.
 * 때문에, 아래와 같이 각각 클래스를 새로 만들어 어드바이저로 만들어주어야 한다.
 */
@Slf4j
public class AspectV5 {

    @Aspect
    @Order(2)
    public static class LogAspect {
        @Around("hello.aop.order.aop.Pointcuts.allOrder()")
        public Object doLog(
                ProceedingJoinPoint joinPoint
        ) throws Throwable {
            log.info("[log] {}", joinPoint.getSignature());
            return joinPoint.proceed();
        }
    }

    @Aspect
    @Order(1)
    public static class TxAspect {
        @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
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
}
