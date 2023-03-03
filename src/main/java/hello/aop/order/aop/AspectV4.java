package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * 스프링 AOP 구현 4 - 포인트컷 참조
 */
@Slf4j
@Aspect
public class AspectV4 {

    /**
     * 외부 클래스의 포인트컷을 가져오려면 풀 패키지로 작성해야한다.
     */
    @Around("hello.aop.order.aop.Pointcuts.allOrder()")
    public Object doLog(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

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
