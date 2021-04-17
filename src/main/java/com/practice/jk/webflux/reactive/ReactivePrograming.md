### Reactive Programming

#### 함수형 프로그래밍

명령형 프로그래밍과는 반대로 '선언형 프로그래밍' 이라는 기법이 있다. 큰 특징은

1. 불변객체를 이용 - parameter 로 받은 값이 있다면, 그 값을 그대로 변형, 반환이 아닌 복사 후 사본의 수정을 통해 원본 객체를 유지
2. side-effect 최대한 제외 - 불변객체에 이어지는 내용, 복사함으로써 불필요한 부수 효과를 줄임
3. 선언적 코드 - 어떻게 해야 하는 코딩보단, 무엇을 해야 하는 코딩

함수형 프로그래밍은 이런 선언형 프로그래밍의 하위 카테고리 형식이라고 생각하면 편하다.

> 명령형 vs 함수형
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
>   Function<String, String> doSomething = a -> "methodA : " + a;
>   
>   public String methodA(String a) {
>     return doSomething.apply(a);
>   }
> }
> ```

#### 리액티브 프로그래밍

리액티브 프로그래밍은 이런 선언형 프로그래밍에 베이스를 두고 event-driven 이 추가되었다고 보면 편하다.

가장 간단하게 이해가 가능한 코드 하나를 예시로 들어보면

```java
public class StreamExampleClass {

  public String searchAndAppend(List<String> needSearch) {
    StringBuilder result = new StringBuilder();

    for (String searchString : needSearch) {
      if (searchString.startsWith("s")) {
        result.append(searchString)
            .append(" | ");
      }
    }

    return result.toString();
  }
}
```

위의 코드에서 전달받은 list 중 s 로 시작하는 단어를 찾고 found 라는 접미어를 붙여 구분자로 파이프라인을 사용해 하나의
string 으로 반환하는 작업을 진행하고 있다.

반면 위의 부분을 리액티브하게 구현한다면

```java
public class StreamExampleClass {
  public Mono<String> searchAndAppend(List<String> needSearch) {
    return Mono.just(
        needSearch.stream()
            .filter(s -> s.startsWith("s"))
            .map(s -> s + " found")
            .collect(Collectors.joining(" | "))
    );
  }
}
```

이와 같이 구성이 된다.

결과값을 저장하기위한 result 객체도 별도로 생성 할 필요 없고, collection 에서 오브젝트를 하나씩 꺼내서 비교하고 다시 builder 에
appending 하는 부분도 필요가 없어진다.

가독성 및 코드 측면에서 유리하고, 앞서 말했듯이 result 객체가 불필요해지는 것처럼 리소스 부분에서도 이득이다.