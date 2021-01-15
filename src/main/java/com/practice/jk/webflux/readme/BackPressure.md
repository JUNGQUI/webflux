### 3. Back Pressure

파이프 내의 유체 역학에서 배압(Back Pressure) 란 정상적인 흐름을 방해하는 요소로, 주로 파이프의 구조적 문제, 유체의 속도 등
여러 역학적 이유로 인해 발생한다.

하지만 reactive stream 에서 back pressure 는 비슷하지만 반대의 뜻을 의미한다.

유압의 흐름을 방해하는 백프레셔가 긍정적으로 사용하는 부분은 바로 request - response 간 OOM 방지에 있다.

후술할 Observer pattern 에서의 문제는 일반적인 push 방식이기 때문에 publisher 가 subscriber 의 상태와 상관 없이
다량의 데이터를 지속적으로 보내게 된다.

이렇게 할 경우 request 에 대해 실질적으로 처리가 되어 response 를 받아도 정상적으로 처리가 되지 않은 것처럼 진행이 될 수 있다.

이러한 부분을 제어하기 위해 만든 개념이 `Back Pressure` 이다.

```java
public class JKObserver {
   @Override
   public void run() {
      for (int i = 1; i <= 10; i++) {
         setChanged();        // 상태 변경
         notifyObservers(i);  // Observable 에 값 전달 (sub 상태에 상관 없이 pub 은 데이터 전달)
      }
   }

   Observer observer = (o, arg) -> {
      integerByObserver.add((Integer) arg);
      System.out.println(arg);
   };
}

public class JKPubSub {

   private static Publisher<Integer> integerPublisher(Iterable<Integer> iterator) {
      return s -> s.onSubscribe(new Subscription() {
      	// ...
        @Override
        public void request(long n) { // 전달받은 n 값에 맞게 해당 개수 만큼 전달
        	try {
               // 현재 이 code 에는 n 값에 의한 제어가 있진 않지만 code 추가 시 제어 가능
               iterator.forEach(s::onNext);
               s.onComplete();
            } catch (Exception ex) {
               s.onError(ex);
            }
         }
         // ...
      });
   }

   private static Subscriber<Integer> logSubscriber() {
      return new Subscriber<Integer>() {
      	// ...
        @Override
        public void onSubscribe(Subscription s) {
        	System.out.println("onSubscribe:");
        	s.request(Long.MAX_VALUE);  // Long.MAX_VALUE 를 전달했지만 실제로 여기서 제어
        }
        // ...
      };
   }
}
```

Observer pattern 을 보면 sub 부분에서 어떠한 개수나 제한을 요청하지 않고, pub 부분에서 로직 처리를 하는 즉시 값을 전달하고 있다.

하지만 pub/sub pattern 을 보면 request 를 하는 subscriber 가 개수를 한정적으로 전달하고 있다. 이를 통해 pub 에 sub 의 상태에 맞게 다이나믹하게 당겨 올 수 있어서
`dynamic pull` 방식이라고도 하며, 이러한 기제를 `Back pressure` 라고 한다.