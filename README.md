# webflux
기초 webflux 공부

## WebFlux?

spring 5 에서부터 리액티브 프로그래밍을 지원하기 위해 도입된 모듈

> 리액티브 프로그래밍?
> 비동기적 데이터 흐름을 처리하는 프로그래밍 방식이라고 볼 수 있다.
> 
> 예컨데,
> - 검색창의 자동 완성 기능
> - 게시글의 좋아요, 댓글 수 등의 즉각적인 반영
> 
> 들이 이와 같다고 볼 수 있다.

## 원리?

Reactive Programming 에서 알아야 할 것들이 많다.

1. reactive stream
2. event-driven(event loop)
3. Back Pressure
4. pattern
   - Observer Pattern
   - Pub - Sub Pattern

`README 를 나눠서 기록하기로 하였다.`

### [1. Reactive Stream](/src/main/java/com/practice/jk/webflux/readme/ReactiveStream.md)

### 2. [Event-Driven (event loop)](/src/main/java/com/practice/jk/webflux/readme/EventDriven.md)

### 3. [Back Pressure](/src/main/java/com/practice/jk/webflux/readme/BackPressure.md)

### 4. [Pattern](/src/main/java/com/practice/jk/webflux/readme/Pattern.md)

### 부록 - [CompletableFuture](/src/main/java/com/practice/jk/webflux/readme/CompletableFuture.md)
