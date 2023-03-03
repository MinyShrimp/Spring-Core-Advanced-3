package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

/**
 * 스프링 AOP 구현 6 - 어드바이스 종류
 */
@Slf4j
@Aspect
public class AspectV6 {

    /*
    @Around("hello.aop.order.aop.Pointcuts.orderAndService()")
    public Object doTransaction(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {
        Signature signature = joinPoint.getSignature();

        try {
            // @Before
            log.info("[트랜잭션 시작] {}", signature);

            Object result = joinPoint.proceed();

            // @AfterReturning
            log.info("[트랜잭션 커밋] {}", signature);
            return result;
        } catch (Exception e) {
            // @AfterThrowing
            log.info("[트랜잭션 롤백] {}", signature);
            throw e;
        } finally {
            // @After
            log.info("[Resource Release] {}", signature);
        }
    }
     */

    /**
     * JoinPoint 호출 이전
     */
    @Before("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doBefore(
            JoinPoint joinPoint
    ) {
        log.info("[before] {}", joinPoint.getSignature());
    }

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

    /**
     * JoinPoint 실행 이후 ( 정상이던 아니던 )
     */
    @After("hello.aop.order.aop.Pointcuts.orderAndService()")
    public void doAfter(
            JoinPoint joinPoint
    ) {
        log.info("[after] {}", joinPoint.getSignature());
    }
}
