### 5. Mono, Flux

#### Reactive

Mono 와 Flux 를 이해하기 전에 사실 이 부분에 대해서 먼저 이해해야 한다.

리액티브 프로그래밍이란 간단하게 함수형 프로그래밍이라고 볼 수 있다.

기존의 가장 간단한 리액티브 중 하나는 흔히 아는 pub-sub 이다.

```java
import org.reactivestreams.Subscriber;

public interface Publisher<T> {

  void subscribe(Subscriber<? super T> subscriber);
}

public interface Subscriber<T> {
  void onSubscribe(Subscription sub);
  void onNext(T item);
  void onError(Throwable ex);
  void onComplete();
}

public interface Subscription {
  void request(long n);
  void cancel();
}
```

pub - sub 을 간단히 살펴보자면 publisher(발행자) 는 어떠한 subscriber(구독자) 를 subscription(구독) 하고
subscriber 는 진행됨에 따라 지속적으로 publisher 에게 결과가 전달된다.

> 구독자의 이벤트?
> 
> publisher 가 subscriber 를 subscribe 하는 과정에서 subscriber 가 이벤트를 발생시킨다 라고 생각을 하고 있었는데,
> 기본적으로 publisher 에서 이벤트가 발생해서 subscriber 를 subscribe 하는거고, 그 안에서 subscription 이라는 process 를 진행하고
> event-driven 방식에 의해 publisher 에게 결과를 전달해준다.

리액티브 스트리밍 (이하 리액터) 은 event-driven 이라는 부분에서 알 수 있듯이 하나의 파이프 라인을 구축하여 형성하는 것이다.

```java
public class main() {
  public void printGreeting(String name) {
    String capitalName = name.toUpperCase();
    String greeting = "Hello, " + capitalName + "!";
    System.out.println(greeting);
  }
}
```

위 코드는 명령형으로 짜여진 함수로 전달받은 이름을 대문자로 변환하고 인사를 붙여서 출력하는 함수다.

하지만 이를 리액터로 변환한다면

```java
import reactor.core.publisher.Mono;

public class main() {

  public void printGreeting(String name) {
    Mono.just(name)
        .map(String::toUpperCase)
        .map(upperName -> "Hello, " + upperName + "!")
        .subscribe(System.out::println);
  }
}
```

이와 같이 구현이 되는데, java stream API 처럼 각 상황 별 process 를 거치고 진행하는 pipe line 적인 성격이 드러난다.

#### Reactive Operation

리액티브 오퍼레이션엔 크게 다음과 같은 분류로 나뉜다.

- creation
- combination
- transformation
- logic

쉽게 말해, just 와 같이 데이터 생성부가 creation 이라 볼 수 있다.

```java
public class testClass {
  @Test
  public void createAFlux_just() {
    Flux<String> fruitFlux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");
    StepVerifier.create(fruitFlux)
        .expectNext("Apple")
        .expectNext("Orange")
        .expectNext("Grape")
        .expectNext("Banana")
        .expectNext("Strawberry")
        .verifyComplete();
  }
}
```

> StepVerifier?
> 
> 일반적으로 Mono, Flux 에 대해 검증 로직을 짜기가 어려운 편인데, 이유는 명령형과 다르게 데이터가 return 이 딱 바로 떨어지지 않기 때문에
> 검증도 리액티브하게 만들어서 진행해야 하는데, 이를 StepVerifier 가 어느정도 커버해준다.

위와 같이 일반적으로 생성할 수 있지만 기존의 Collection 에서도 `.fromArray()` 이나 `.fromIterable()` 등으로 생성이 가능하다.
