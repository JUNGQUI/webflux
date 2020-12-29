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

### 1. Reactive Stream

stream 이란 연속적인 데이터의 흐름을 뜻한다. 이러한 흐름의 앞에 Reactive 가 접두로 붙었다는 것은 말 그대로
연속적인 데이터를 `즉각 반응` 할 수 있게 만들었다는 뜻이다.

다음은 [리액티브 스트림](http://reactive-streams.org) 에서 이야기 하는 정의다.

> Reactive Streams is an initiative to provide a standard for asynchronous stream processing with non-blocking back pressure.

쉽게 말해 `Non-blocking, Back pressure 를 이용한 비동기 stream 처리의 표준` 이라고 볼 수 있다.

위 예시로 들었듯이 검색창의 자동완성 기능의 경우 입력되는 단어에 대해 유추되는 검색어가 즉각적으로 보여지는 기능의 경우 
검색어가 즉각적으로 client 에 의해 수시로 변경되기에 각 요청에 대해 thread 가 server 의 반응을 모두 기다렸다가 전달하기엔
매우 낮은 performance 를 보여준다. 

이러한 처리들을 위한 표준이 Reactive stream 이라 볼 수 있다.

### 2. Event-Driven (event loop)

> event-driven
> 
> 기존의 방식이 thread 가 request 를 전달받았을 경우 처음부터 끝까지 (response 까지) 책임을 져 다른 작업을 하지 못하는 반면
> event-driven 방식은 request 를 받아 service 에 전달하고 해당 thread 는 다른 작업을 진행하되 response 가 만들어졌다면 event 를 발생시켜
> thread 에게 response 를 client 에 전달하는 방식을 의미한다.

기존의 전통적인 servlet base MVC spring 을 보면 이와 같다.

- I/O 발생 시 결과값을 받을때까지 대기
- thread 가 결과값 전달까지 점유

이와 같은 구조일 경우 thread 가 점유된 상태에서 아무런 일을 안하는 이슈가 있고 이는 곧 resource 낭비로 이어지게 된다.

반면 reactive 방식으로 구축할 경우 asynchronous 에 Non-blocking IO 를 따르기에 thread 가 놀게 되는 것도 줄고
그만큼 효율적으로 많은 data 변경점을 부담없이 사용 할 수 있다.

> Synchronous / Asynchronous | Blocking / Non-Blocking
> 
> - Synchronous : 동기 방식, flow 가 응답 여부, 순서에 맞게 진행
> - Asynchronous : 비동기 방식, flow 가 응답 여부와 관계 없이 다음 단계로 진행 
> - Blocking : 블로킹 방식, 결과 값을 받아와서 사용을 한다.
> - Non-Blocking : 논-블로킹 방식, 결과 값을 굳이 받지 않아도 진행에 이상 없다.
> Synchronous 와 Asynchronous 는 jQuery 의 ajax 가 가장 대표적인 예시고, 이해하기도 쉽지만 block, non-block 의 경우 이해가 쉽지 않은데, 아래의 내용을 보자.
> 
> ```java
> public class Main {
>   public int plus(int a, int b) {
>     return a + b;
>   }
> 
>   public void plus(int a, int b) {
>     System.out.println(a + b);
>   }
> }
> ```
> 
> 굳이 이해를 쉽게 하기 위해 이와 같이 구성을 하였는데, int 를 return 으로 받는 경우 blocking 방식이라 볼 수 있고 void 의 경우 non-blocking 방식이라 볼 수 있다. 
> 
> 즉, 결과값을 받아서 실제로 사용을 즉시에 해야 하는 경우가 바로 blocking 이라 볼 수 있고, 어떠한 방식으로든 추후에 사용이 가능하며 request 측에서 신경을 쓰지 않는 경우가 non-blocking 이라고 볼 수 있다.
>
> non-blocking 은 request 를 받는 server 의 resource 에 조금 더 신경을 썼다고 생각하면 편리하다.

여기에서 call-back 을 이용해서 하는 방식이 event-loop 방식이라고 볼 수 있다.

> event loop
> event 가 발생하는지 loop 를 돌면서 확인 후 event 발생 시 call-back 으로 응답을 하는 방식이다. 
> 이러한 방식의 이름을 따 event-loop 방식이라고 명명한다.

이러한 event-loop 를 사용하기에 webflux 의 경우 asyc - Non-blocking 을 활용해 thread 및 resource 자원을 아낄 수 있고 reactive 한 web response 가 가능해진다.

그리고 또하나의 pattern 이 있는데 publish - subscribe pattern (pub - sub) 이 적용된다.

용어 그대로 발급 - 구독 패턴인데 발급은 request 를 받은 server, 구독은 request 를 보낸 client 로 볼 수 있고, 
server 에서 할당받은 work 가 끝났을 때 (publisher) 
event-loop 를 통해 요청한 client (subscriber) 에게 response 를 보내게 된다.

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

이때 이 onComplete 가 완료되었다는 것을 publisher 에 전달하는 것으로 한번의 request 를 종료하게 된다.

> 당연하게도, 이렇게 구현을 한다 하더라도 양쪽 모두 비동기 형식을 취하지 않는다면 비동기형으로 동작하지 않는다.
> 
> 가장 흔한 예로 flux 에서 .log() 형식을 취해 로그를 기록하는 비동기 처리를 만들었으나 실제 기존의 동기 방식에 대비해 큰 차이가 있지 않은 이슈가 있었다.
> 
> 왜 그런지 원인 파악을 하던 중 .log() 가 동기식 I/O 였기에 발생한 성능 이슈였다.
> 따라서 이와 같이 설계를 할 때 주의해야 한다. 

## 장점

앞서 언급했듯이 불필요한 resource 소모를 아껴 reactive 한 구도를 가져갈 수 있다.

1. react 의 render 처럼 small data 에 대해 reactive 하게 구동 시킬 수 있다.
2. resource 감소로 동 spec 대비 많은 traffic 처리가 가능하다.

## 단점

1. 굉장히 한정적인 상황 (business logic 적으로) 에서만 사용 가능하다.
2. async - Non-blocking 이기에 중간에 blocking 을 하나라도 잘못 쓰면 더더욱 느려진다.
  - Flux 를 이용해서 사용하던 도중 .log 를 사용 시 극단적으로 performance 가 떨어진다. 이유는 log method 는 blocking IO 이기에 이 method 때문에 효율이 안나오게 된다.
