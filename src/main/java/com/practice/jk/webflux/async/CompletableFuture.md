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

하지만 문제는 해당 Future 로 이루어진 다수의 method 를 하나의 method 에서 호출하고, 이 결과를 합쳐서 보여주는 로직이 있다고 가정해보자.

```java
public class Test {
  void futureAsyncTest() throws ExecutionException, InterruptedException {
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
    
    // ...
  }
}
```

이 코드의 경우 실행할 경우 각기 다른 thread 가 지정되어 있기에 각자 비동기로 작동하게 될 것이다.

하지만 각각의 결과를 합치기 위해 각자 get 을 진행하기 때문에 만약 서로 다른 작업을 수행했고 그 작업의 시간을 동시에 끝난다는 가정을 하기
어렵다면, 두 결과를 하나로 합치는 로직은 수행이 안될 것이다.

이런 두 가지 이상의 로직을 동시에 수행해서 결과로 합산하기 위해 CompletableFuture 가 생겨났다.