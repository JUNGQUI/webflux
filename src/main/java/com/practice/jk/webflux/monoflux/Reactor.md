### Reactor

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

1. creation

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

> 범용적으로 collections 를 예시로 들었으나 실제로 timer 를 이용해서 초당 1씩 증가한 값을 넣는등 활용도가 다양하다.
> 
> 다만 실제로 쓰일 일은 별로 없어 collections 를 예시로 들었다.

2. combination

조합을 하는 부분인데 A 와 B 로직을 조합하여 C 의 결과를 도출하는 등 비즈니스 로직에서 쓰일 법한 상황에서 서로 다른 리액터로 구성하여
한번에 해결이 가능하다.

```java
import java.time.Duration;

public class Combination {

  public void combinationFlux() {
    Flux<String> cFlux = Flux.just("Garfield", "Kojak", "Barbossa")
        .delayElements(Duration.ofMillis(500));
    Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")
        .delaySubscription(Duration.ofMillis(250))
        .delayElements(Duration.ofMillis(500));
    
    Flux<String> mergedFlux = cFlux.mergeWith(foodFlux);
  }
}
```

상표 flux `cFlux` 와 음식 flux `foodFlux` 를 각기 다른 식으로 구성을 했다 가정하고 추후 결과를 취합 할 때 merge 를 이용해서 하나의
결과처럼 사용이 가능하다.

> 위 예시에서 delay 를 사용한 이유는 일반적으로 Flux 는 가장 빠른 데이터부터 방출을 하는데, 현재 test 목적은 잘 병합되었는지를 확인하는 것이기에
> 결과를 임의로 조작하기 위해 추가하였다. 
> 
> 상표가 0.5초, 음식도 0.5초 간격으로 값이 방출되지만 음식의 0.25초의 딜레이를 두고 나중에 시작되기에
> 결과값은 가필드-라자냐, 코작-롤리팝, 바르보사-사과 와 같이 순서로 출력될 것이다.

사실 위와 같은 상황에서 순서가 중요하다면 (보통 A 로직과 B 로직의 서로 다른 결과물을 조합하여 결과로 도출하기에) `zip` 을 활용 할 수 있다.

```java
import java.time.Duration;

public class Combination {

  public void combinationFlux() {
    Flux<String> cFlux = Flux.just("Garfield", "Kojak", "Barbossa")
        .delayElements(Duration.ofMillis(500));
    Flux<String> foodFlux = Flux.just("Lasagna", "Lollipops", "Apples")
        .delaySubscription(Duration.ofMillis(250))
        .delayElements(Duration.ofMillis(500));
    
    Flux<Tuple2<String, String>> mergedFlux = Flux.zip(cFlux, foodFlux);
    
    StepVerifier.create(mergedFlux)
        .expectNextMatches(p -> 
            p.getT1().equals("Garfield")
                && p.getT2().equals("Lasagna"))
        .expectNextMatches(p -> 
            p.getT1().equals("Kojak")
                && p.getT2().equals("Lollipops"))
        .expectNextMatches(p -> 
            p.getT1().equals("Barbossa")
                && p.getT2().equals("Apples"))
    .verifiyComplete();
  }
}
```

이런식으로 zip 을 활용하면 순서에 맞게 배치를 할 뿐 아니라 여러 Flux 를 동시에 구독, 진행 시킬 수 있다.

3. transformation, logic
   
Mono, Flux 를 이용하다보면 데이터의 변형이 필요할 때가 있다. 그러한 연산이 사실 logic 과도 연관이 있다고 볼 수 있는데
java Stream API 를 이용하듯이 리액터에서도 이러한 것들을 이용 할 수 있다.

대표적으로 filter, distinct, first, skip(N), take(N) 등 도 사용 가능하며 map 도 이용 가능 하다.

```java
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
  String firstName;
  String lastName;
}

public class JustMapTest {

  public void mapTest() {
    Flux<Player> playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
        .map(n -> {
          String[] splitName = n.split(" ");
          return new Player(splitName[0], splitName[1]);
        });
    
    StepVerifier.create(playerFlux)
        .expectNext(new Player("Michael", "Jordan"))
        .expectNext(new Player("Scottie", "Pippen"))
        .expectNext(new Player("Steve", "Kerr"))
        .verifyComplete();
  }
}
```

##### 비동기 처리

리액터에서 비동기로 데이터 가공을 할 경우엔 map 이 아닌 flatMap + scheduler 로 조합해서 사용해야 한다.

예컨데 위의 예시의 경우 순차적으로 마이클, 스코티, 스티브가 적용이 되는데 저 하나의 player 를 만드는데 비용이 너무 크다면 비동기로 돌리는게
합당하다.

```java
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.scheduler.Schedulers;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

  String firstName;
  String lastName;
}

public class JustMapTest {

  List<Player> testList = Arrays.asList(
      new Player("Michael", "Jordan")
      , new Player("Scottie", "Pippen")
      , new Player("Steve", "Kerr")
  );

  public void mapTest() {
    Flux<Player> playerFlux = Flux.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
        .flatMap(n -> Mono.just(n)
            .map(p -> {
              String[] splitName = p.split(" ");
              return new Player(splitName[0], splitName[1]);
            })
            .subscribeOn(Schedulers.parallel())
        );

    StepVerifier.create(playerFlux)
        .expectNextMatches(p -> testList.contains(p))
        .expectNextMatches(p -> testList.contains(p))
        .expectNextMatches(p -> testList.contains(p))
        .verifyComplete();
  }
}
```

위와 같이 flatMap 은 Flux 로 만들어진 부분에 대해 다시 한번 Mono (혹은 Flux) 로 감싸서 진행하는데, 이 순간부터 내부에 새로운
Mono/Flux 객체가 생기게 되므로 이 부분에 대해 subscribeOn(Schedulers.parallel()) 을 이용해서 비동기적 처리가 가능하다.

전체를 볼때 이 함수는 한번의 로직을 수행하지만 내부에서 Flux 안에 있는 값들을 새로운 Mono 로 감싸면서 여러 개로 구분을 지은 셈이고,
이렇게 구분 지어 놓은 로직 하나 하나를 subscribeOn 을 통해 병렬 처리 하게끔 만든것이다.

모든 내부 Mono 가 끝나게 되면 그 결과들을 하나의 Flux 로 다시 모아서 playerFlux 가 결과를 받게 된다.
