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