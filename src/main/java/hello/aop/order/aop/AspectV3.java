package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 스프링 AOP 구현 3 - 어드바이스 추가
 */
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
    ) throws Throwable {
        log.info("[log] {}", joinPoint.getSignature());
        return joinPoint.proceed();
    }

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
