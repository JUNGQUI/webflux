package com.practice.jk.webflux.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.just;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
public class RouterController {

  @Bean
  public RouterFunction<?> helloRouterFunction() {
    return route(GET("/hello")
        , request -> ok().body(
            just("SOME RESPONSE"), String.class
        ));
  }
}