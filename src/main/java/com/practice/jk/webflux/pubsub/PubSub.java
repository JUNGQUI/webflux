package com.practice.jk.webflux.pubsub;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PubSub {
	public static void main(String[] args) throws InterruptedException {
		Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5);
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Publisher<Integer> publisher = new Publisher<Integer>() {
			@Override public void subscribe(Subscriber<? super Integer> s) {

				// 여러 subscriber 가 존재할 수 있기 때문에 각 subscriber 에 대응하기 위해 iterator 를 각 subscribe 에서 만든다.
				Iterator<Integer> iterator = iterable.iterator();

				s.onSubscribe(new Subscription() {
					@Override public void request(long n) {
						executorService.execute(() -> {
							int i = 0;

							try {
								while(i++ <= n) {
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
			}
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
		executorService.awaitTermination(30, TimeUnit.SECONDS);
		executorService.shutdown();
	}
}
