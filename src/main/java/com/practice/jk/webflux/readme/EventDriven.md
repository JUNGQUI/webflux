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