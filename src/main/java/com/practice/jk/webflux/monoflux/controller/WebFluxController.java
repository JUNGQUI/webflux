package com.practice.jk.webflux.monoflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class WebFluxController {

  WebClient webClient = WebClient.create();

  @GetMapping(value = "/rest")
  public Mono<String> rest (@RequestParam(value = "idx") int idx) {
//    Mono<ClientResponse> result = webClient.get()         // method 정의
//        .uri("http://localhost:8080/service?req={req}", idx)              // url 지정
//        .exchange();  // 실행
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
        .flatMap(cr -> cr.bodyToMono(String.class));
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
