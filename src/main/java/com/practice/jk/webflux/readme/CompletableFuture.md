### 부록 - CompletableFuture

java 에서 async 하게 접근이 가능한 부분이 있는데 java 5 에서부터 지원하게된 Future 이다.

```java
public class JKFuture {
	public static void SimpleFuture() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Future<Double> doubleFuture = executorService.submit(() -> {
			someLongWork();
			return 1D;
		});

		someOtherWork();

		try {
			doubleFuture.get(1, TimeUnit.SECONDS);
		} catch (Exception ex) {
			System.out.println("There is error Occur!");
		}
	}

	public static void SimpleFutureWithoutTime() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Future<Double> doubleFuture = executorService.submit(() -> {
			someLongWork();
			return 1D;
		});

		someOtherWork();

		try {
			doubleFuture.get();
		} catch (Exception ex) {
			System.out.println("There is error Occur!");
		}
	}

	private static void someOtherWork() {
		System.out.println("Some Other work" + Thread.currentThread());
	}

	private static void someLongWork() throws InterruptedException {
		Thread.sleep(5000);
		System.out.println("Some Long work" + Thread.currentThread());
	}
}
```

해당 코드에서 `someLongWork()` 와 `someOtherWork()` 는 서로 다른 thread 에서 실행되며,
`someLongWork()` 의 경우 많은 작업량을 표기하기 위해 5초의 지연을 걸어놓았다.

이렇게 한 뒤 

`SimpleFuture()` get 에 타임아웃을 걸고 `SimpleFutureWithoutTime()` 의 get 에는 타임아웃을 걸지 않게 되면
걸지 않은 곳의 경우 에러가 발생하지 않지만 타임아웃을 건 경우 에러가 발생한다. 이 이유는 해당 thread 로 실행하는
비동기의 경우 최대 타임아웃이 걸려 있기 때문에 실행 중 블로킹이 멈춰지게 된다.

비동기의 경우 상황이 좋은 경우 높은 퍼포먼스를 제공하지만 반대로 상황이 좋지 않을 경우 무한정 기다리게 되는 경우가 발생할 수 있기 때문에
이러한 케이스를 방지하기 위해 타임아웃을 설정하는 편이 좋다.

하지만 한계가 명확한데, 오래전 만들어진 비동기 처리기이다 보니 get, complete 등과 같이 굉장히 심플한 method 만 사용이 가능하며
이를 사용하는 개발자도 명확한 로직 (답이 바로 나오는) 구조일때만 사용이 용이한 단점이 있다.

이러한 부분을 보완한것이 `CompletableFuture` 이다.

```java
public class JKCompletableObject {
	private String id;
	private String password;

	public JKCompletableObject(String id, String password) {
		this.id = id;
		this.password = password;
	}

	public static JKCompletableObject saveObject(String id, String password) throws InterruptedException {
		// 계정을 DB 에 저장하는데 1초 소요된다고 가정한다.
		Thread.sleep(1000);
		return new JKCompletableObject(id, password);
	}
}
```

이와 같은 object 가 있다고 가정하자. 보다시피 saveObject method 는 DB 에 다녀오는 작업으로 임의로 정의하였고
sleep 을 걸어 딜레이를 임의로 만들어냈다.

```java
public class JKCompletableFuture {
	// sync
	List<JKCompletableObject> localList = jkCompletableObjectList.stream()
			.map(jkCompletableObject ->
					{
						try {
							return JKCompletableObject.saveObject(
									jkCompletableObject.getId(),
                                jkCompletableObject.getPassword()
                            );
						} catch (InterruptedException e) {
							e.printStackTrace();
							return jkCompletableObject;
						}
					}
			).collect(Collectors.toList());
	
	// parallel
	List<JKCompletableObject> localList = jkCompletableObjectList.parallelStream()
			.map(jkCompletableObject ->
					{
						try {
							return JKCompletableObject.saveObject(
									jkCompletableObject.getId(),
                                jkCompletableObject.getPassword()
                            );
						} catch (InterruptedException e) {
							e.printStackTrace();
							return jkCompletableObject;
						}
					}
			).collect(Collectors.toList());
	
	// async
	List<CompletableFuture<JKCompletableObject>> completableFutureList = jkCompletableObjectList.stream()
			.map(jkCompletableObject ->
					CompletableFuture.supplyAsync(() -> {
						try {
							return JKCompletableObject.saveObject(
									jkCompletableObject.getId(),
                                jkCompletableObject.getPassword()
                            );
						} catch (InterruptedException e) {
							e.printStackTrace();
							return jkCompletableObject;
						}
					}))
			.collect(Collectors.toList());
	
	List<JKCompletableObject> localList = completableFutureList.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList());
}
```

저장을 할 때 소요되는 시각은 각각 5초, 1초, 1초 정도 소요되는데 sync 와 parallel 의 경우 일반적으로 많이 접하는
방식이기에 설명을 스킵하고, CompletableFuture 만 본다면,

우선 `CompletableFuture.supplyAsync()` 를 통해 수행할 작업에 대해 정의하고, 내부 로직을 작성한다.

이후에 해당 completableFuture 를 실행시키고 해당 결과 내역을 합치는(join) 과정을 통해서 결과를 동일하게 출력한다.

이렇게 할 경우 기존의 간단한 작업만 수행했던 부분에 대해 내부에 exception 에 대해서도 유동적으로 조절이 가능하며 안에 다른 로직을 추가로
생성할 수 있다.

