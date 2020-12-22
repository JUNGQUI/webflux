package com.practice.jk.webflux.monoflux;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

public class MonoFlux {
	public static void simpleFlux() {
		Flux.just(1, 2, 3)
				.doOnNext(i -> System.out.println("doOnNext:" + i)) // subscriber.onNext()
				.subscribe(i -> System.out.println("subscribe:" + i));  // publisher.subscribe()
	}

	public static void publishByFlux() {
		Flux<Integer> flux = Flux.just(1, 2, 3);

		flux.subscribe(new Subscriber<Integer>() {
			@Override public void onSubscribe(Subscription s) {
				s.request(Long.MAX_VALUE);
			}

			@Override public void onNext(Integer integer) {
				System.out.println("onNext:" + integer);
			}

			@Override public void onError(Throwable t) {
				System.out.println("onError:" + t.getMessage());
			}

			@Override public void onComplete() {
				System.out.println("onComplete");
			}
		});
	}
}
