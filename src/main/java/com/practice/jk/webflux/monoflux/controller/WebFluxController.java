package com.practice.jk.webflux.monoflux.controller;

import com.practice.jk.webflux.monoflux.service.AsyncService;
import com.practice.jk.webflux.monoflux.service.MonoFluxService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
public class WebFluxController {

  private final AsyncService asyncService;
  private final MonoFluxService monoFluxService;

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
      System.out.println(Thread.currentThread().getId() + " thread running");
      Thread.sleep(3000);
    } catch (Exception ex) {
      System.out.println("J Tag, error " + ex.getMessage());
    }

    return req + " service1";
  }

  @GetMapping(value = "/service2")
  public String service2(@RequestParam(value = "req") String req) {
    try {
      System.out.println(Thread.currentThread().getId() + " thread running");
      Thread.sleep(5000);
    } catch (Exception ex) {
      System.out.println("J Tag, error " + ex.getMessage());
    }

    return req + " service2";
  }

  @GetMapping(value = "/service3")
  public List<String> service3(@RequestParam(value = "req") String req) {
    return new ArrayList<>(Arrays.asList(req, "this", "is", "flux"));
  }

  @GetMapping(value = "/mono/zip/service")
  public Mono<String> zipDirect() {
    try {
      return monoFluxService.multiMono();
    } catch (Exception ex) {
      return Mono.just(ex.getMessage());
    }
  }

  @GetMapping(value = "/flux/service")
  public Flux<String> fluxMultiThread() {
    return monoFluxService.multiMonoWithFlux();
  }

  @GetMapping(value = "/flux/service/serverResponse")
  public Mono<ServerResponse> fluxMultiThreadServerResponse() {
    return ServerResponse.ok().body(BodyInserters.fromValue(monoFluxService.multiMonoWithFlux()));
  }

  @GetMapping(value = "/mono/zip/controller")
  public Mono<String> zipFromController() {
    try {
      Mono<String> firstMono = Mono.just(monoFluxService.longService()).subscribeOn(Schedulers.parallel());
      Mono<String> secondMono = Mono.just(monoFluxService.smallService()).subscribeOn(Schedulers.parallel());

      return Mono.zip(
          firstMono, secondMono
      ).flatMap(tuple ->
          Mono.just(tuple.getT1() + " " + tuple.getT2())
      ).subscribeOn(Schedulers.parallel());
    } catch (Exception ex) {
      return Mono.just(ex.getMessage());
    }
  }
}
