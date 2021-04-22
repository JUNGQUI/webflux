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
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    Future<Double> result1 = executorService.submit(() -> {
      try {
        JKFuture.someLongWork();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      return 1D;
    });

    Future<Double> result2 = executorService.submit(() -> {
      try {
        JKFuture.someLongWork();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      return 2D;
    });

  }
}
```