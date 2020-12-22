package com.practice.jk.webflux.pubsub;

import java.util.function.Function;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DeligateSubscriber implements Subscriber<Integer> {

  Subscriber subscriber;

  public DeligateSubscriber(Subscriber subscriber) {
    this.subscriber = subscriber;
  }

  @Override
  public void onSubscribe(Subscription s) {
    subscriber.onSubscribe(s);
  }

  @Override
  public void onNext(Integer integer) {
    subscriber.onNext(integer);
  }

  @Override
  public void onError(Throwable t) {
    subscriber.onError(t);
  }

  @Override
  public void onComplete() {
    subscriber.onComplete();
  }
}
