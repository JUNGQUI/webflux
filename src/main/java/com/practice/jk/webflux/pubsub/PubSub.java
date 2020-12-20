package com.practice.jk.webflux.pubsub;

import java.util.Arrays;
import java.util.Iterator;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PubSub {
	public static void main(String[] args) {
		Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5);

		Publisher<Integer> publisher = new Publisher<Integer>() {
			@Override public void subscribe(Subscriber<? super Integer> s) {
				Iterator<Integer> iterator = iterable.iterator();

				s.onSubscribe(new Subscription() {
					@Override public void request(long n) {
						if (iterator.hasNext()) {
							s.onNext(iterator.next());
						} else {
							s.onComplete();
						}
					}

					@Override public void cancel() {

					}
				});
			}
		};

		Subscriber<Integer> subscriber = new Subscriber<Integer>() {
			@Override public void onSubscribe(Subscription s) {
				System.out.println("onSubscribe");
				s.request(Long.MAX_VALUE); // 모든 값을 요청, 값의 요청 개수라고 볼 수 있나?
			}

			@Override public void onNext(Integer integer) {
				System.out.println("onNext" + integer);
			}

			@Override public void onError(Throwable t) {
				System.out.println("onError");
			}

			@Override public void onComplete() {
				System.out.println("onComplete");
			}
		};

		publisher.subscribe(subscriber);
	}
}
