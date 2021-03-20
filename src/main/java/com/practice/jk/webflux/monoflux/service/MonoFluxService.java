package com.practice.jk.webflux.monoflux.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class MonoFluxService {

  public String longService() {
    try {
      this.logging("long");
      Thread.sleep(2000);
      this.logging("long");
      return "long";
    } catch (Exception ex) {
      return "long";
    }
  }

  public String smallService() {
    try {
      this.logging("small");
      Thread.sleep(1000);
      this.logging("small");
      return "small";
    } catch (Exception ex) {
      return "small";
    }
  }

  public Mono<String> multiMono() {
    Mono<String> firstMono = Mono.just(this.longService());
    Mono<String> secondMono = Mono.just(this.smallService());

    return Mono.zip(firstMono, secondMono)
        .flatMap(t -> Mono.just(t)
            .map(tuple -> tuple.getT1() + "\n" + tuple.getT2())
            .subscribeOn(Schedulers.parallel())
        );
  }

  public Flux<String> multiMonoWithFlux() {
    return Flux.just("1", "2", "3")
        .flatMap(n -> Mono.just(n)
            .map(p -> p + " " + this.longService() + " " + this.smallService() + "\n")
            .subscribeOn(Schedulers.parallel())
        );
  }

  private void logging(String requester) {
    System.out.println(
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")) + " "
            + Thread.currentThread().getId() + " "
            + requester);
  }
}
