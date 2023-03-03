package hello.aop.order.aop;

import org.aspectj.lang.annotation.Pointcut;

/**
 * 스프링 AOP 구현 4 - 포인트컷 참조
 * <p>
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
