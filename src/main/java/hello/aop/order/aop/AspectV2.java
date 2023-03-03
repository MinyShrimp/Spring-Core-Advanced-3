package hello.aop.order.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 스프링 AOP 구현 2 - 포인트컷 분리
 */
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
