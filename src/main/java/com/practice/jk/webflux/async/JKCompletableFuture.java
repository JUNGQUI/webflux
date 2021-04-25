package com.practice.jk.webflux.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JKCompletableFuture {

	private final Executor threadPoolTaskExecutor;

	public List<JKCompletableObject> jkCompletableObjectList = Arrays.asList(
			new JKCompletableObject("id1", "password1"),
			new JKCompletableObject("id2", "password2"),
			new JKCompletableObject("id3", "password3"),
			new JKCompletableObject("id4", "password4"),
			new JKCompletableObject("id5", "password5")
	);

	public List<JKCompletableObject> syncList() {
		long startTime = System.nanoTime();

		List<JKCompletableObject> localList = jkCompletableObjectList.stream()
				.map(jkCompletableObject ->
						{
							try {
								return JKCompletableObject.saveObject(jkCompletableObject.getId(), jkCompletableObject.getPassword());
							} catch (InterruptedException e) {
								e.printStackTrace();
								return jkCompletableObject;
							}
						}
				).collect(Collectors.toList());

		System.out.println("Sync List : " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

		return localList;
	}

	public List<JKCompletableObject> syncListParallel() {
		long startTime = System.nanoTime();

		List<JKCompletableObject> localList = jkCompletableObjectList.parallelStream()
				.map(jkCompletableObject ->
						{
							try {
								return JKCompletableObject.saveObject(jkCompletableObject.getId(), jkCompletableObject.getPassword());
							} catch (InterruptedException e) {
								e.printStackTrace();
								return jkCompletableObject;
							}
						}
				).collect(Collectors.toList());

		System.out.println("Sync List parallel : " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

		return localList;
	}

	public List<JKCompletableObject> asyncCompletableFuture() {
		long startTime = System.nanoTime();

		List<CompletableFuture<JKCompletableObject>> completableFutureList = jkCompletableObjectList.stream()
				.map(jkCompletableObject ->
						CompletableFuture.supplyAsync(() -> {
							try {
								return JKCompletableObject.saveObject(jkCompletableObject.getId(), jkCompletableObject.getPassword());
							} catch (InterruptedException e) {
								e.printStackTrace();
								return jkCompletableObject;
							}
						}))
				.collect(Collectors.toList());

		List<JKCompletableObject> localList = completableFutureList.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList());

		System.out.println("Async List : " + TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS));

		return localList;
	}

	public List<String> syncJoinIdAndPassword(List<JKCompletableObject> jkCompletableObject) {
		return jkCompletableObject.stream()
				.map(this::joinIdAndPassword)
				.collect(Collectors.toList());
	}

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
		System.out.println(Thread.currentThread() + " intercept work");
		CompletableFuture<List<String>> oddResult = this.completableFutureJoinIdAndPassword(odd);

		CompletableFuture<List<String>> finalResult = CompletableFuture.allOf(evenResult, oddResult)
				.thenApplyAsync(aVoid -> {
					List<String> e = evenResult.join();
					System.out.println(Thread.currentThread() + " intercept work, in allof");
					List<String> o = oddResult.join();
					List<String> r = new ArrayList<>();
					r.addAll(e);
					r.addAll(o);

					return r;
				});

		return finalResult.join();
	}

	private String joinIdAndPassword(JKCompletableObject jkCompletableObject) {
		try {
			Thread.sleep(1000);
		} catch (Exception ex) {

		}

		return jkCompletableObject.getId() + "|" + jkCompletableObject.getPassword();
	}

	private CompletableFuture<List<String>> completableFutureJoinIdAndPassword(List<JKCompletableObject> jkCompletableObject) {
		System.out.println(Thread.currentThread() + " intercept work, in private");
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
