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
```

pub - sub 을 간단히 살펴보자면 publisher(구독자) 는 어떠한 subscriber(구독매체) 를 subscription(구독) 하고
subscriber 는 진행됨에 따라 지속적으로 publisher 에게 결과가 전달된다.