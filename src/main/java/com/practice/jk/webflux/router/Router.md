### 함수형 요청 핸들러

우선 router 에 대해 이해하기 전에, 함수형 프로그래밍, 함수형 요청 핸들러에 대해 알아야 한다.

> 함수형 프로그래밍?
> 
> 프로그래밍 패러다임에 명령형 / 함수형 이렇게 크게 두가지 케이스로 나뉘는데 명령형은 우리가 흔히 아는 구조이다.
> 
> request 대상에서 특정 method 를 호출하고, 호출당한 method 는 그대로 다시 값을 돌려주는, 일반적인 구조이다.
> 
> ```java
> public class commandProgramming {
>   public String methodA(String a) {
>     a = "methodA : " + a;
>     return a;
>   }
> }
> ```
> 
> ```java
> import java.util.function.Function;
> 
> public class functionalProgramming {
> Function<String, String> doSomething = a -> "methodA : " + a;
>   public String methodA(String a) {
>     return doSomething.apply(a);
>   }
> }
> ```