### 4. Pattern

#### Observer Pattern

Observer 와 Observable 이란 객체가 존재한다. Observer 는 Observable 을 구독하고 Observable 은 request 를 받아 처리 후
Observer 에게 response 를 전달한다.

여기까지 보면 일반적인 MVC 와 다를 바 없지만 차이점은 위에 언급한 event-loop 이다.

```java
// 참고 : java 9 부터 Observable 은 deprecated 되었다.
public class Ob extends Observable implements Runnable {
	
	// 수행할 비즈니스 로직
	@Override
	public void run() {
		for (int i = 1; i <= 10; i++) {
			setChanged();
			notifyObservers(i);
		}
	}
}

public class Observer {
	public void observer() {
       List<Integer> integerByObserver = new ArrayList<>();

       Observer observer = (o, arg) -> {
       	  // 비즈니스 로직을 통해 전달 받은 값에 대한 처리
          integerByObserver.add((Integer) arg);
          System.out.println(arg);
       };

       Ob intObserver = new Ob();
       intObserver.addObserver(observer); // Observable 추가
       intObserver.run();                 // Observer 실행해서 Observable 에서 처리하게 run
    }
}
```

일반적인 MVC 내의 흐름은 a = BUSINESS_LOGIC(); 을 통해 값을 전달 받는 반면, Observer pattern 은
call-back 으로 전달 받아 처리할 부분을 구현하고(Observer) 값을 요청할 Business logic class 에 등록(`Observable.addObserver(Observer)`)한다.

이렇게 하면 `비동기적으로 연속적인 데이터` 에 대헤 처리가 가능해진다.

#### Publisher - Subscriber Pattern

Publisher - Subscriber Pattern 의 경우 Observer pattern 의 개선된 부분이라고 볼 수 있다.

사실 Observer Pattern 에는 큰 issue 가 있는데,

1. Subscriber 의 상황과 관계 없는 publisher

Observer Pattern 의 비동기 스트림 처리가 매우 좋긴 하지만 하나의 문제가 바로 비동기이다.

위의 Observable 을 다시 한번 보자.

```java
// 참고 : java 9 부터 Observable 은 deprecated 되었다.
public class Ob extends Observable implements Runnable {
	// 수행할 비즈니스 로직
	@Override
	public void run() {
		for (int i = 1; i <= 10; i++) {
			setChanged();
			notifyObservers(i);
		}
	}
}
```

Observer 에 별다른 부하 여부와 상관 없이 반복문을 돌면서 바로 notifyObservers 를 통해 값을 전달한다.

예시가 가벼운 로직이기에 문제는 없지만 전달된 데이터가 처리되는게 무거운 로직을 가지고 있다면 받아들이는 Observer 에 부하가 발생해 문제가 생길 여지가 있다.

2. Error Handling

위와 비슷한 예로 Observer 에서 Error 가 발생하면 Observable 에 별다른 처리 없이 이후의 데이터를 받지 못한다.

이럴 경우 주는 입장에선 다 줬다고 판단하고 받는 입장에선 정지된 부분에서부터 다시 요청을 해야하는 불상사가 있는데, 코드 상으로 구현 할 수 있는 방법이 없다.

위의 두 가지 케이스를 처리하기 위해 Publisher - Subscriber pattern 에는 다양한 method 가 있다.

```java
public class PublisherSubscriber {
	public void pubsub() {
       Publisher<Integer> publisher = new Publisher<Integer>() {

          @Override public void subscribe(Subscriber<? super Integer> s) {
             s.onSubscribe(new Subscription() {

                @Override public void request(long n) {
                   s.onNext(1);
                   s.onComplete();
                }

                @Override public void cancel() {
                   s.onError();
                }
             });
          }
       };
       
       publisher.subscribe(integerSubscriber());  // 구독
    }
    
    public Subscriber<Integer> integerSubscriber() {
       return new Subscriber<Integer>() {
          Subscription subscription; // subscribe 를 저장하기 위해 subscription 수정

          @Override public void onSubscribe(Subscription s) {
             System.out.println("onSubscribe");
             this.subscription = s;
             this.subscription.request(1); // business logic
          }

          @Override public void onNext(Integer integer) {
             System.out.println("onNext" + integer);
             this.subscription.request(1);
          }

          @Override public void onError(Throwable t) {
             System.out.println("onError");
          }

          @Override public void onComplete() {
             System.out.println("onComplete");
          }
       };
    }
}
```

위에서 `publisher.subscribe(subscriber())` 를 통해 subscriber() 가 publisher 를 구독하고
구현한 publisher 에서 전달받은 subscriber 를 통해 request 를 받을 경우 next, 완료되었다 가정하고 complete를 던진다.

```java
public class request_onNext {
   @Override public void request(long n) {
     for (int i = 0; i < n; i++) {
        s.onNext(1);     // Subscriber.onNext(PARAMETER) 를 통해 값 전달 
     }

     s.onComplete();  // 완료 후 완료 처리
   }

   @Override public void onNext(Integer integer) {
      System.out.println("onNext" + integer);   // 비즈니스 로직 처리
      this.subscription.request(1);             // Subscriber 가 구독하는 subscription (즉, publisher) 에
      // request 를 보내는데, Subscriber 가 감당할 수 있는 개수인 1을 전달
   }
}
```

위 코드와 같이 publisher 는 완료 후 onComplete() 를 통해 subscriber 에 완료 시그널을 보낼 수 있고, subscriber 의 경우 자신이 감당 가능한 수준의
resource 를 publisher 에게 요청하게 되는데, 현재 코드에서는 무조건 1개의 값만을 보내게 되어 있다.

> 당연하게도, 이렇게 구현을 한다 하더라도 양쪽 모두 비동기 형식을 취하지 않는다면 비동기형으로 동작하지 않는다.
>
> 가장 흔한 예로 flux 에서 .log() 형식을 취해 로그를 기록하는 비동기 처리를 만들었으나 실제 기존의 동기 방식에 대비해 큰 차이가 있지 않은 이슈가 있었다.
>
> 왜 그런지 원인 파악을 하던 중 .log() 가 동기식 I/O 였기에 발생한 성능 이슈였다.
> 따라서 이와 같이 설계를 할 때 주의해야 한다. 