package com.practice.jk.webflux.async;

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

	public List<String> asyncJoinIdAndPassword(List<JKCompletableObject> jkCompletableObject) {

		return jkCompletableObject.stream()
				.map(this::joinIdAndPassword)
				.collect(Collectors.toList());
	}

	private String joinIdAndPassword(JKCompletableObject jkCompletableObject) {
		return jkCompletableObject.getId() + "|" + jkCompletableObject.getPassword();
	}

	private CompletableFuture<String> completableFutureJoinIdAndPassword(JKCompletableObject jkCompletableObject) {
		return CompletableFuture.supplyAsync(() -> jkCompletableObject.getId() + "|" + jkCompletableObject.getPassword());
	}
}
