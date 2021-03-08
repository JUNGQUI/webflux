package com.practice.jk.webflux.monoflux.service;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class MonoFluxService {

  public String longService() throws Exception {
    Thread.sleep(2000);
    return Thread.currentThread().getId() + " long";
  }

  public String smallService() throws Exception {
    Thread.sleep(1000);
    return Thread.currentThread().getId() + " small";
  }

  public Mono<String> multiMono() throws Exception {
    return Mono.zip(
        Mono.just(this.longService()).subscribeOn(Schedulers.parallel())
        , Mono.just(this.smallService()).subscribeOn(Schedulers.parallel()))
        .flatMap(tuple -> Mono.just(
            tuple.getT1()
                + tuple.getT2()
        ));
  }
}
