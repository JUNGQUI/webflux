package com.practice.jk.webflux.monoflux.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class MonoFluxService {

  public String longService() throws Exception {
//    this.logging("long");
//    Thread.sleep(2000);
    this.logging("long");
    return Thread.currentThread().getId() + " long";
  }

  public String smallService() throws Exception {
//    this.logging("small");
//    Thread.sleep(1000);
    this.logging("small");
    return Thread.currentThread().getId() + " small";
  }

  public Mono<String> multiMono() throws Exception {
    return Mono.zip(
        Mono.just(this.longService())
            .subscribeOn(Schedulers.parallel())
        , Mono.just(this.smallService())
            .subscribeOn(Schedulers.parallel())
    )
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
