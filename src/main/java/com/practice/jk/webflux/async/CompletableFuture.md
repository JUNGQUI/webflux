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


이런 두 가지 이상의 로직을 동시에 수행해서 결과로 합산하기 위해 CompletableFuture 가 생겨났다.

CompletableFuture 의 경우 Future 와 CompleteStage 를 상속받아 구현된 객체이다.

> CompleteStage
> 
> [docs](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletionStage.html) 를 보면 
> 가장 앞에 `A stage of a possibly asynchronous computation, that performs an action or computes a value when another CompletionStage completes.` 이런
> 문구가 있다. 즉, stage 는 비동기적으로 다른 Complete stage 가 완료 후에 계산이 가능하다는 뜻으로 위에서 언급했듯이 여러 비동기 Future 의 서로 다른
> 시간 복잡도를 가진 경우를 조합해서 사용하기에 적합하다는 뜻이다.

바로 본론으로 들어가서, CompletableFuture 는 이와 같이 사용이 가능하다.

```java
public class CompletableFutureJoin {
  public List<String> asyncJoinIdAndPassword(List<JKCompletableObject> jkCompletableObject) {
    int i = 0;
    List<JKCompletableObject> odd = new ArrayList<>();
    List<JKCompletableObject> even = new ArrayList<>();

    for (JKCompletableObject jk : jkCompletableObject) {
      if (i % 2 == 0) {
        even.add(jk);
      } else {
        odd.add(jk);
      }

      i++;
    }

    CompletableFuture<List<String>> evenResult = this.completableFutureJoinIdAndPassword(even);
    CompletableFuture<List<String>> oddResult = this.completableFutureJoinIdAndPassword(odd);

    CompletableFuture<List<String>> finalResult = CompletableFuture.allOf(evenResult, oddResult)
        .thenApplyAsync(aVoid -> {
          List<String> e = evenResult.join();
          List<String> o = oddResult.join();
          List<String> r = new ArrayList<>();
          r.addAll(e);
          r.addAll(o);

          return r;
        });

    return finalResult.join();
  }

  private CompletableFuture<List<String>> completableFutureJoinIdAndPassword(List<JKCompletableObject> jkCompletableObject) {
    try {
      Thread.sleep(1000);
    } catch (Exception ex) {

    }

    return CompletableFuture.supplyAsync(() ->
            jkCompletableObject.stream()
                .map(jk -> jk.getId() + "|" + jk.getPassword())
                .collect(Collectors.toList())
        , threadPoolTaskExecutor);
  }
}
```

private 로 만들어진 `completableFutureJoinIdAndPassword` 를 보면 긴 작업으로 가정하기 위해 Thread sleep 을 통해 
딜레이를 걸어두었고 리턴 값으로 CompletableFuture 를 반환한다. 

`CompletableFuture.supplyAsync` 의 경우 파라메터가 (작업, thread) 로 지정이 되는데 threadPool 에서 작업 가능한
thread 를 할당해주었고 작업은 전달받은 Collection stream 에서 id, password 를 하나의 string 으로 합치는 작업을 전달했다.

`asyncJoinIdAndPassword` 에선 이러한 하나의 method 를 2개의 CompletableFuture 로 작성하였고 다시 하나의 CompletableFuture
로 join 하여 결과를 반환한다.

이렇게 할 경우 각기 다른 CompletableFuture 작업에 대해 비동기로 처리하되, 모든 작업이 완료된 이후 하나의 결과로 합쳐서 전달하게 된다.

`CompletableFuture<Double> finalResult = CompletableFuture.allOf(result1, result2)` 를 보면 result1, result2 를 받아서 최종 결과값이 나오기 전까지 기다리며, 

chaining 을 통해 연결된 메소드 `thenApplyAsync` 내에서 결과값을 합쳐서 새로운 result 로 뽑아내는 것을 볼 수 있다.

이와 같이 Future 와는 다르게 CompletableFuture 는 CompletableStage 를 상속받아 만들어졌기에 서로 다른 비동기 처리에 대한 결과를
비동기로 다시 하나의 결과로 조합해서 사용이 가능하다.
