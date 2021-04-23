### CompletableFuture

그렇다면 CompletableFuture 는 무엇일까? Future 의 업데이트 버전으로 볼 수 있다.

[여기 기존의 코드](./Future.md)를 한번 살펴보자.

```java
public class JKFuture {
	private static final int timeout = 5;
	public static void SimpleFuture() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Future<Double> doubleFuture = executorService.submit(() -> {
			someLongWork();
			return 1D;
		});

		someOtherWork();

		try {
			doubleFuture.get(timeout, TimeUnit.SECONDS);
		} catch (Exception ex) {
			System.out.println("There is error Occur!");
		}
	}
	
	private static void someOtherWork() {
	  System.out.println("Some Other work" + Thread.currentThread());
	}
	
	public static void someLongWork() throws InterruptedException {
		Thread.sleep((timeout - 1) * 1000);
		System.out.println("Some Long work" + Thread.currentThread());
	}
}
```

해당 코드는 얼핏 보기엔 이상이 없어보인다.

하지만 문제는 서로 다른 동시성에 대해 문제가 발생한다는건데, 해당 Future 로 이루어진 다수의 method 를 하나의 method 에서 호출하고, 이 결과를 합쳐서 보여주는 로직이 있다고 가정해보자.

```java
public class Test {
  Double futureAsyncTest() throws ExecutionException, InterruptedException {
    ExecutorService executorService1 = Executors.newSingleThreadExecutor();
    ExecutorService executorService2 = Executors.newSingleThreadExecutor();

    Future<Double> result1 = executorService1.submit(() -> {
      try {
        JKFuture.someLongWork();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      return 1D;
    });

    Future<Double> result2 = executorService2.submit(() -> {
      try {
        JKFuture.someLongWork();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      return 2D;
    });

    CompletableFuture<Double> finalResult = CompletableFuture.allOf(result1, result2)


    return finalResult.join();
  }
}
```
기존의 코드의 경우 각자 비동기로 작동한다 하더라도 결과값에 대해 return 할 경우 각자 다른 작업시간으로 인해 서로 다른 결과값을 반환 할 경우가 빈번하게 될 것이다.

그에 반해 위 코드의 경우 실행할 경우 각기 다른 thread 가 지정되어 있기에 각자 비동기로 작동하는데, 그 이유로는 마지막 줄에 key 가 있다.

`CompletableFuture<Double> finalResult = CompletableFuture.allOf(result1, result2)` 를 보면 result1, result2 를 받아서 최종 결과값이 나오기 전까지 기다리며, 
