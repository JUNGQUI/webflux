package com.practice.jk.webflux.monoflux.controller;

import com.practice.jk.webflux.monoflux.service.AsyncService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
public class WebFluxController {

  private final AsyncService asyncService;

  WebClient webClient = WebClient.create();

  @GetMapping(value = "/rest/mono")
  public Mono<String> mono (@RequestParam(value = "idx") int idx) {
    return Mono.zip(webClient.get()
        .uri("http://localhost:8080/service?req={req}", idx)
        .retrieve()
        .bodyToMono(String.class)
        .subscribeOn(Schedulers.boundedElastic()),
        webClient.get()
            .uri("http://localhost:8080/service2?req={req}", idx)
            .retrieve()
            .bodyToMono(String.class)
            .subscribeOn(Schedulers.boundedElastic())
        )
        // asyncService
        .flatMap(request -> Mono.fromCompletionStage(
            asyncService.asyncService(request.toList()
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "))
            ))
        );
  }

  @GetMapping(value = "/rest/mono/error")
  public String monoSingle (@RequestParam(value = "idx") int idx) {
    // webflux 로 구현 시 block 하면 error 발생
    return Mono.zip(webClient.get()
            .uri("http://localhost:8080/service?req={req}", idx)
            .retrieve()
            .bodyToMono(String.class)
            .subscribeOn(Schedulers.boundedElastic()),
        webClient.get()
            .uri("http://localhost:8080/service2?req={req}", idx)
            .retrieve()
            .bodyToMono(String.class)
            .subscribeOn(Schedulers.boundedElastic())
    )
        // asyncService
        .flatMap(request -> Mono.fromCompletionStage(
            asyncService.asyncService(request.toList()
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "))
            ))
        ).flux().toStream().findFirst().orElse("ERROR MUST BE OCCURRED!!");
  }

  @GetMapping(value = "/rest/flux")
  public Flux<String> flux (@RequestParam(value = "idx") int idx) {
    return webClient.get()
        .uri("http://localhost:8080/service3?req={req}", idx)
        .exchangeToFlux(request -> request.bodyToFlux(String.class));
  }

  @GetMapping(value = "/rest/flux/event-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> fluxEventStream () {
    return Flux.fromIterable(Arrays.asList("string1", "string2", "string3"));
  }

  @GetMapping(value = "/service")
  public String service(@RequestParam(value = "req") String req) {
    try {
      Thread.sleep(3000);
    } catch (Exception ex) {
      System.out.println("J Tag");
    }

    return req + " service1";
  }

  @GetMapping(value = "/service2")
  public String service2(@RequestParam(value = "req") String req) {
    try {
      Thread.sleep(3000);
    } catch (Exception ex) {
      System.out.println("J Tag");
    }

    return req + " service2";
  }

  @GetMapping(value = "/service3")
  public List<String> service3(@RequestParam(value = "req") String req) {
    return new ArrayList<>(Arrays.asList(req, "this", "is", "flux"));
  }
}
