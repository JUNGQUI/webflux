package com.practice.jk.webflux.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
