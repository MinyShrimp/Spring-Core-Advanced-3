package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

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

    /**
     * public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
     * 과 정확하게 매칭
     */
    @Test
    @DisplayName("helloMethod와 정확하게 매칭")
    void exactMatch() {
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        assertThat(
                pointcut.matches(helloMethod, MemberServiceImpl.class)
        ).isTrue();
    }

    @Test
    @DisplayName("모두 매칭")
    void allMatch() {
        pointcut.setExpression("execution(* *(..))");
        assertThat(
                pointcut.matches(helloMethod, MemberServiceImpl.class)
        ).isTrue();
    }

    @Nested
    @DisplayName("메서드 이름 매칭")
    class MethodNameMatches {
        @Test
        @DisplayName("정확한 메서드 이름 매칭")
        void nameMatch() {
            pointcut.setExpression("execution(* hello(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("hel로 시작하는 메서드 이름 매칭")
        void nameMatchStar1() {
            pointcut.setExpression("execution(* hel*(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("메서드 중간에 el이 들어가는 메서드 매칭")
        void nameMatchStar2() {
            pointcut.setExpression("execution(* *el*(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("메서드 이름 매칭 실패")
        void nameMatchFalse() {
            pointcut.setExpression("execution(* non(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isFalse();
        }
    }

    @Nested
    @DisplayName("패키지 매칭")
    class PackageMatches {
        @Test
        @DisplayName("정확한 패키지 + 타입 + 메서드 이름 매칭")
        void packageExactMatch1() {
            pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("정확한 패키지 매칭")
        void packageExactMatch2() {
            pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("정확한 패키지 매칭 - 하위 타입 포함 안됨")
        void packageExactMatchFalse() {
            pointcut.setExpression("execution(* hello.aop.*.*(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isFalse();
        }

        @Test
        @DisplayName("현재 패키지와 하위 타입 모두 매칭")
        void packageMatchSubPackage1() {
            pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("현재 패키지와 하위 타입 모두 매칭 2")
        void packageMatchSubPackage2() {
            pointcut.setExpression("execution(* hello.aop..*.*(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }
    }

    @Nested
    @DisplayName("타입 매칭")
    class TypeMatches {
        Method internalMethod;

        @BeforeEach
        public void init() throws NoSuchMethodException {
            internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        }

        @Test
        @DisplayName("정확한 타입 매칭")
        void typeExactMatch() {
            pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(String))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("부모 타입 매칭")
        void typeMatchSuperType() {
            pointcut.setExpression("execution(* hello.aop.member.MemberService.*(String))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("자식에만 있는 타입 - 자식이 확인")
        void typeMatchInternal() {
            pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(String))");
            assertThat(
                    pointcut.matches(internalMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("자식에만 있는 타입 - 부모가 확인")
        void typeMatchNoSuperTypeMethodFalse() {
            pointcut.setExpression("execution(* hello.aop.member.MemberService.*(String))");
            assertThat(
                    pointcut.matches(internalMethod, MemberServiceImpl.class)
            ).isFalse();
        }
    }

    @Nested
    @DisplayName("파라미터 매칭")
    class ParamMatched {
        @Test
        @DisplayName("파라미터 갯수 1개, String 타입 허용")
        void exactMatch() {
            pointcut.setExpression("execution(* *(String))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("파라미터가 없어야 함")
        void noArgsMatch() {
            pointcut.setExpression("execution(* *())");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isFalse();
        }

        @Test
        @DisplayName("파라미터 갯수 1개, 모든 타입 허용")
        void oneArgsMatchStar() {
            pointcut.setExpression("execution(* *(*))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("파라미터 갯수 2개, 모든 타입 허용")
        void twoArgsMatchStar() {
            pointcut.setExpression("execution(* *(*, *))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isFalse();
        }

        @Test
        @DisplayName("파라미터 갯수 무관, 모든 타입 허용")
        void argsMatchAll() {
            pointcut.setExpression("execution(* *(..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }

        @Test
        @DisplayName("첫 번째 파라미터의 타입이 String, 파라미터 갯수 무관, 모든 타입 허용")
        void argsMatchComplex() {
            pointcut.setExpression("execution(* *(String, ..))");
            assertThat(
                    pointcut.matches(helloMethod, MemberServiceImpl.class)
            ).isTrue();
        }
    }
}
