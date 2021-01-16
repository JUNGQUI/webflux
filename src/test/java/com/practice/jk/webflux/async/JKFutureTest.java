package com.practice.jk.webflux.async;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JKFutureTest {
	@Test
	void futureTest() {
		JKFuture.SimpleFuture();
	}

	@Test
	void futureWithoutTimeTest() {
		JKFuture.SimpleFutureWithoutTime();
	}

	@Test
	void completableFutureTest() {
		List<JKCompletableObject> sync = JKCompletableFuture.syncList();
		List<JKCompletableObject> syncParallel = JKCompletableFuture.syncListParallel();
		List<JKCompletableObject> async = JKCompletableFuture.asyncCompletableFuture();

		for (int i = 0; i < JKCompletableFuture.jkCompletableObjectList.size(); i++) {
			Assertions.assertEquals(JKCompletableFuture.jkCompletableObjectList.get(i), sync.get(i));
			Assertions.assertEquals(JKCompletableFuture.jkCompletableObjectList.get(i), syncParallel.get(i));
			Assertions.assertEquals(JKCompletableFuture.jkCompletableObjectList.get(i), async.get(i));
		}
	}
}