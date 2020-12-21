package com.practice.jk.webflux.pubsub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PubSubTest {

	@Test
	void pubSubTest() throws InterruptedException {
		Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5);
		List<Integer> copyIterable = new ArrayList<>();

		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Publisher<Integer> publisher = s -> {

			// 여러 subscriber 가 존재할 수 있기 때문에 각 subscriber 에 대응하기 위해 iterator 를 각 subscribe 에서 만든다.
			Iterator<Integer> iterator = iterable.iterator();

			s.onSubscribe(new Subscription() {
				@Override public void request(long n) {
					executorService.execute(() -> {
						int i = 0;

						try {
							while(i++ < n) {
								if (iterator.hasNext()) {
									s.onNext(iterator.next());
								} else {
									s.onComplete();
									break;
								}
							}
						} catch (RuntimeException e) {
							// error 처리도 우아하게
							s.onError(e);
						}
					});
				}

				@Override public void cancel() {

				}
			});
		};

		Subscriber<Integer> subscriber = new Subscriber<Integer>() {
			Subscription subscription; // 위의 iterator 와 마찬가지, subscribe 를 저장하기 위해 subscription 수정

			@Override public void onSubscribe(Subscription s) {
				System.out.println("onSubscribe");
				this.subscription = s;
				this.subscription.request(1); // business logic
			}

			@Override public void onNext(Integer integer) {
				System.out.println("onNext" + integer);
				copyIterable.add(integer);
				this.subscription.request(1);
			}

			@Override public void onError(Throwable t) {
				System.out.println("onError");
			}

			@Override public void onComplete() {
				System.out.println("onComplete");
			}
		};

		publisher.subscribe(subscriber);
		// thread 종료까지 기다려주지 않기에 error 발생, 5초 유예 추가
		executorService.awaitTermination(1, TimeUnit.SECONDS);
		executorService.shutdown();

		Iterator<Integer> result = iterable.iterator();

		for (int i = 0; i < 5; i++) {
			Assertions.assertEquals(result.next(), copyIterable.get(i));
		}

	}
}