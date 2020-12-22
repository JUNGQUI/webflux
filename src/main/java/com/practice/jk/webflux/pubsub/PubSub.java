package com.practice.jk.webflux.pubsub;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class PubSub {
	public static void pubSub() {
		Iterable<Integer> iterable = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

		Publisher<Integer> publisher = new Publisher<Integer>() {
			@Override public void subscribe(Subscriber<? super Integer> s) {

				// 여러 subscriber 가 존재할 수 있기 때문에 각 subscriber 에 대응하기 위해 iterator 를 각 subscribe 에서 만든다.
				Iterator<Integer> iterator = iterable.iterator();

				s.onSubscribe(new Subscription() {
					@Override public void request(long n) {
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
					}

					@Override public void cancel() {

					}
				});
			}
		};

		publisher.subscribe(integerSubscriber());
//		publisher.subscribe(logSubscriber());
	}

	public static void simplePubSub () {
		Iterable<Integer> iterable = Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList());
		Publisher<Integer> publisher = integerPublisher(iterable);
		publisher.subscribe(logSubscriber());
	}

	public static void mapPublisher() {
		Iterable<Integer> iterable = Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList());
		Publisher<Integer> publisher = integerPublisher(iterable);
		Publisher<Integer> mapPublisher = mapPublisher(publisher, i -> i * 10);
		mapPublisher.subscribe(logSubscriber());
	}

	public static void sumPublisher() {
		Iterable<Integer> iterable = Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList());
		Publisher<Integer> publisher = integerPublisher(iterable);
		Publisher<Integer> sumPublisher = sumPublisher(publisher);
		sumPublisher.subscribe(logSubscriber());

	}

	private static Publisher<Integer> mapPublisher(
			Publisher<Integer> prevPublisher, Function<Integer, Integer> function
	) {
		return s -> prevPublisher.subscribe(
//				new Subscriber<Integer>() {
//					@Override
//					public void onSubscribe(Subscription subscription) {
//						s.onSubscribe(subscription);
//					}
//
//					@Override
//					public void onNext(Integer integer) {
//						s.onNext(function.apply(integer));
//					}
//
//					@Override
//					public void onError(Throwable t) {
//						s.onError(t);
//					}
//
//					@Override
//					public void onComplete() {
//						s.onComplete();
//					}
//				}
				new DeligateSubscriber(s) {
					@Override
					public void onNext(Integer integer) {
						s.onNext(function.apply(integer));
					}
				}
				);
	}

	private static Publisher<Integer> sumPublisher(Publisher<Integer> prevPublisher) {
		return s -> prevPublisher.subscribe(new DeligateSubscriber(s) {
			int sum = 0;

			@Override
			public void onNext(Integer integer) {
				sum += integer;
			}

			@Override
			public void onComplete() {
				s.onNext(sum);
				s.onComplete();
			}
		});
	}

	private static Publisher<Integer> integerPublisher(Iterable<Integer> iterator) {
		return s -> s.onSubscribe(new Subscription() {
			@Override
			public void request(long n) {
				try {
					iterator.forEach(s::onNext);
					s.onComplete();
				} catch (Exception ex) {
					s.onError(ex);
				}
			}

			@Override
			public void cancel() {

			}
		});
	}

	private static Publisher<Integer> reducePublisher(Publisher<Integer> prevPublisher) {
		return new Publisher<Integer>() {
			@Override
			public void subscribe(Subscriber<? super Integer> s) {

			}
		};
	}

	private static Subscriber<Integer> logSubscriber() {
		return new Subscriber<Integer>() {
			@Override
			public void onSubscribe(Subscription s) {
				System.out.println("onSubscribe:");
				s.request(Long.MAX_VALUE);
			}

			@Override
			public void onNext(Integer integer) {
				System.out.println("onNext:" + integer);
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("onError:" + t.getMessage());
			}

			@Override
			public void onComplete() {
				System.out.println("onComplete");
			}
		};
	}

	private static Subscriber<Integer> integerSubscriber() {
		return new Subscriber<Integer>() {
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
	}
}
