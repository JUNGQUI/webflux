package com.practice.jk.webflux.async;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JKFutureTest {

	@Autowired
	private JKCompletableFuture jkCompletableFuture;

	@Test
	void futureTest() {
		JKFuture.SimpleFuture();
	}

	@Test
	void futureAsyncTest() throws ExecutionException, InterruptedException {
		ExecutorService executorService1 = Executors.newSingleThreadExecutor();
		ExecutorService executorService2 = Executors.newSingleThreadExecutor();
		ExecutorService executorService3 = Executors.newSingleThreadExecutor();
		ExecutorService executorService4 = Executors.newSingleThreadExecutor();

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

		Future<Double> result3 = executorService3.submit(() -> {
			try {
				JKFuture.someLongWork();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return 3D;
		});

		Future<Double> result4 = executorService4.submit(() -> {
			try {
				JKFuture.someLongWork();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return 4D;
		});

		System.out.println("Some Other Work");
		Assertions.assertEquals(1D, result1.get());
		System.out.println("Intercepted Some Other Work1");
		Assertions.assertEquals(2D, result2.get());
		System.out.println("Intercepted Some Other Work2");
		Assertions.assertEquals(3D, result3.get());
		Assertions.assertEquals(4D, result4.get());
		System.out.println("Intercepted Some Other Work3");
	}

	@Test
	void futureWithoutTimeTest() {
		JKFuture.SimpleFutureWithoutTime();
	}

	@Test
	void completableFutureTest() {
		List<JKCompletableObject> sync = jkCompletableFuture.syncList();
		List<JKCompletableObject> syncParallel = jkCompletableFuture.syncListParallel();
		List<JKCompletableObject> async = jkCompletableFuture.asyncCompletableFuture();

		for (int i = 0; i < jkCompletableFuture.jkCompletableObjectList.size(); i++) {
			Assertions.assertEquals(jkCompletableFuture.jkCompletableObjectList.get(i), sync.get(i));
			Assertions.assertEquals(jkCompletableFuture.jkCompletableObjectList.get(i), syncParallel.get(i));
			Assertions.assertEquals(jkCompletableFuture.jkCompletableObjectList.get(i), async.get(i));
		}
	}
}