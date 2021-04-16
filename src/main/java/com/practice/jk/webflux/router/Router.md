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

그리고, 이 함수형을 설명했듯이 router 는 함수형 프로그래밍의 기법을 따른다.

- RequestPredicate : 처리될 요청의 종류를 선언
- RouterFunction : RequestMapping 역할과 핸들러 및 param 지정
- ServerRequest : HTTP request 로 header, body 사용가능
- ServerResponse : HTTP response 로 header, body 사용가능

아래에 전통적인 RequestMapping 을 이용한 controller 와 RouterFunction 을 이용한 예시가 있다.

두 가지 모두 동일한 request와 response 를 받는다.

```java
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestController {
  
  @RequestMapping(value = "/hello")
  public String hello() {
    return "SOME RESPONSE";
  }
} 
```

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterController {

  @Bean
  public RouterFunction<?> helloRouterFunction() {
    return route(GET("/hello")
        , request -> ok().body(
            just("SOME RESPONSE"), String.class
        ));
  }
}
```

두 가지 모두 GET method 로 "/hello" 로 요청 시 "SOME RESPONSE" 라는 결과를 리턴한다.

구조상이나 가독성 측면에서도 (익숙한 부분도 있겠지만) 보편적인 mvc controller 가 더 좋아 보인다.
그런데 왜 이런식으로 함수형으로 넘어가는게 좋다고들 할까?

가장 큰 장점은 reactive stream 형식을 end point 에도 적용하여 데이터의 불변성을 보장하는 것이다. 위에서는 just 를 통해서 mono 로 단순하게 표현하고자 바로 사용했지만,
보통 저 위치에 Function/BiFunction 등이 위치하게 되는데, Function 을 이용하면 데이터가 변질될 가능성이 없다. ~~Function 이 잘 되어 있다는 전제 하에~~

또한 여러 조건들을 같이 사용 할 경우 코드가 점점 복잡해지고 길어질수록 오히려 장점이 된다.

#### nest

앞서 이야기한 것처럼 route 를 통해 controller 역할을 수행 할 수 있다고 했는데 여러가지를 체이닝해서 사용 할 경우 헷갈릴수도 있고
일괄적으로 처리하지 못할 경우 가독성에 문제가 생긴다. 그럴 때 사용하는게 nest 다.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterController {

  @Bean
  public RouterFunction<?> helloRouterFunction() {
    return nest(path("/user")
        , route(
            GET("/hello"), request -> ok().body(
                just("SOME RESPONSE"), String.class
            )
        ).andRoute(
            GET("/hi"), request -> ok().body(
                just("SOME RESPONSE HI"), String.class)
        ).andRoute(
            POST("/helloHi"), request -> ok().body(
                just("SOME RESPONE HELLO AND HI"), String.class)
            )
        );
  }
}
```

위와 같이 구현을 한 경우 최상위 path 에 `/user` 가 있고, 그 아래로 `/hello`, `/hi`, `/hellohi` 가 있다.

이와 같이 하나의 최상위 path 아래에 여러 controller 를 붙일 경우 nest 를 쓴다.

또한 이렇게 하나로 묶어서 구현을 할 경우 `path` 함수를 사용하였기에 체이닝을 통해 or, and, accept 등 다양한 조건을 통해 controller
구현을 할 수 있으며 spring security 를 통해 url 권한 제어 또한 적용된다.