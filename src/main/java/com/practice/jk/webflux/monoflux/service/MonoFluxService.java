package com.practice.jk.webflux.monoflux.service;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class MonoFluxService {

  public Mono<String> longService() throws Exception {
    Thread.sleep(2000);
    return Mono.just(Thread.currentThread().getId() + " long").subscribeOn(Schedulers.boundedElastic());
  }

  public Mono<String> smallService() throws Exception {
    Thread.sleep(1000);
    return Mono.just(Thread.currentThread().getId() + " small").subscribeOn(Schedulers.boundedElastic());
  }

  public Mono<String> multiMono() throws Exception {
    return Mono.zip(this.longService(), this.smallService())
        .flatMap(tuple -> Mono.just(
            tuple.getT1()
                + tuple.getT2()
        ));
  }
}
