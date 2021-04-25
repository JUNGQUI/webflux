package com.practice.jk.webflux.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class JKFuture {
	private static final int timeout = 5;
	public static void SimpleFuture() {
		ExecutorService executorService1 = Executors.newSingleThreadExecutor();
		ExecutorService executorService2 = Executors.newSingleThreadExecutor();

		Future<Double> doubleFuture1 = executorService1.submit(() -> {
			someLongWork();
			return 1D;
		});

		Future<Double> doubleFuture2 = executorService2.submit(() -> {
			someLongWork();
			return 1D;
		});

		someOtherWork();

		try {
			doubleFuture1.get(timeout, TimeUnit.SECONDS);
			doubleFuture2.get(timeout, TimeUnit.SECONDS);
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

	public static void someLongWork() throws InterruptedException {
		System.out.println("Some Long work " + Thread.currentThread());
		Thread.sleep((timeout - 1) * 1000);
	}
}
