package com.practice.jk.webflux.monoflux.controller;

import com.practice.jk.webflux.monoflux.service.AsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class WebFluxController {

  private final AsyncService asyncService;

  WebClient webClient = WebClient.create();

  @GetMapping(value = "/rest")
  public Mono<String> rest (@RequestParam(value = "idx") int idx) {
//    Mono<ClientResponse> result = webClient.get()         // method 정의
//        .uri("http://localhost:8080/service?req={req}", idx)              // url 지정
//        .exchange();  // 실행, 실행을 하게 되면 publisher.subscribe 와 동일한 효과가 spring boot 를 통해 처리된다.
//
//    // 1.
//    ClientResponse clientResponse = null;
//    Mono<String> body = clientResponse.bodyToMono(String.class);
//
//    // 2.
//    Mono<String> bodyToResult = result.flatMap(cr -> cr.bodyToMono(String.class));
//    // 1 과 2 는 동일

    return webClient.get()
        .uri("http://localhost:8080/service?req={req}", idx)
        .exchange()
        .flatMap(cr -> cr.bodyToMono(String.class))
        .flatMap(
            string -> webClient.get()
                .uri("http://localhost:8080/service2?req={req}", string)
                .exchange()
        )
        .flatMap(cr -> cr.bodyToMono(String.class))
        // asyncService
        .flatMap(request -> Mono.fromCompletionStage(
            asyncService.asyncService(request))
        );
  }

  @GetMapping(value = "/service")
  public String service(@RequestParam(value = "req") String req) {
    return req + " service1";
  }

  @GetMapping(value = "/service2")
  public String service2(@RequestParam(value = "req") String req) {
    return req + " service2";
  }
}
