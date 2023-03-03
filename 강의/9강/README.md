# 스프링 AOP 개념

## 프로젝트 생성

### Spring initializer

* https://start.spring.io/
* 프로젝트 선택
    * Project: Gradle - Groovy
    * Language: Java 17
    * Spring Boot: 3.0.3
* Project Metadata
    * Group: hello
    * Artifact: aop
    * Packaging: Jar
* Dependencies
    * Lombok

### build.gradle

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    // 테스트에서 lombok 사용
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
    
    // 스프링 AOP 추가
    implementation 'org.springframework.boot:spring-boot-starter-aop'
}
```

## AOP 소개 - 핵심 기능과 부가 기능

## AOP 소개 - Aspect

## AOP 적용 방식

## AOP 용어 정리

## 정리