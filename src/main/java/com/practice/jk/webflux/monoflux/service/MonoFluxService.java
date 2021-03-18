package com.practice.jk.webflux.monoflux.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class MonoFluxService {

  public String longService() throws Exception {
    this.logging("long");
    Thread.sleep(2000);
    this.logging("long");
    return "long";
  }

  public String smallService() throws Exception {
    this.logging("small");
    Thread.sleep(1000);
    this.logging("small");
    return "small";
  }

  public Mono<String> multiMono() throws Exception {
    Mono<String> firstMono = Mono.just(this.longService())
        .subscribeOn(Schedulers.boundedElastic());
    Mono<String> secondMono = Mono.just(this.smallService())
        .subscribeOn(Schedulers.boundedElastic());

    return Mono.zip(firstMono, secondMono)
        .subscribeOn(Schedulers.parallel())
        .flatMap(
            tuple -> Mono.just(tuple.getT1() + "\n" + tuple.getT2())
        );
  }

  private void logging(String requester) {
    System.out.println(
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")) + " "
            + Thread.currentThread().getId() + " "
            + requester);
  }
}
