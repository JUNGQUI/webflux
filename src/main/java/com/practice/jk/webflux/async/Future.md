### Future

1.5 버전부터 있어온 유구한 역사(?) 를 자랑하는 java 비동기 처리 방식이다.

```java
interface ArchiveSearcher { String search(String target); }
class App {
  ExecutorService executor = ...
  ArchiveSearcher searcher = ...
  
  void showSearch(final String target) throws InterruptedException {
    Future<String> future = executor.submit(new Callable<String>() {
      public String call() {
        return searcher.search(target);
      }
    });
    
    displayOtherThings(); // do other things while searching
    
    try {
      displayText(future.get()); // use future
    } catch (ExecutionException ex) { cleanup(); return; }
  }
}
```

위 코드는 오라클에 있는 설명에 있는 예제 코드인데, Future 를 통해서 값을 받을 때 executor.submit 을 통해서 함수를 실행한다.
여기서 함수는 Callable 을 이용하는데 안에 실제 수행하는 로직이 들어가 있다.

그리고 `displayOtherThings()` 를 보다시피 다른 일을 진행하고 추후 `future.get()` 을 통해 비동기로 실행된 결과 값을 받아온다.

> Runnable, Callable
> 
> 두 가지 모두 Thread 를 상속받아 구현하는데, Runnable 의 경우 값의 리턴 이 없고 예외 처리가 불가능하지만,
> Callable 의 경우 값의 리턴과 Callable 안에서 예외처리가 가능하다.
> 
> 기본적으로 execute.submit 의 경우 Thread 콜을 통해 비동기로 돌리기 때문에 두 가지 모두 사용 가능하니 상황에 따라 사용하면 된다.
> 
> 또한 람다식 표현이 가능하기에 간단하게 구현도 가능하다.

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

위 예제는 실제 쓸만한 로직으로 구현을 한것이다. `someLongWork` (이하 긴 작업) 의 경우 말 그대로 긴 작업으로 예를 들었고
`someOtherWork` (이하 다른 작업) 의 경우 그 외의 작업을 표현하였다.

로직을 수행하게 되면 실제로는 긴 작업을 비동기로 수행하게 하고 이후 다른 작업을 바로 들어가게 된다. 

그렇게 수행하고 나서 .get() 메소드를 호출하면 여기서부터는 블록으로 걸리게 된다. 고로 긴 작업을 미리 수행하고 여타 다른 작업을 수행하고
나중에 긴 작업의 결과가 필요할 경우 get 을 호출해서 값을 가져와서 다시 나머지 로직을 수행하게 된다.

현재 여기서는 타임아웃을 설정하여 그 시간 내에 작업이 되지 않으면 끊게 만들었지만 실제로는 get 을 수행하는 순간부터
동기로 작업을 진행하기에 리소스 낭비를 줄이고 작업 시간을 최적화 할 수 있다.

### CompletableFuture