package com.practice.jk.webflux.reactive.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static reactor.core.publisher.Mono.just;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterController {

  @Bean
  public RouterFunction<ServerResponse> routerFunction() {
    return route(GET("/hello")
        , request -> ok().body(
            just("SOME RESPONSE"), String.class
        ));
  }

  @Bean
  public RouterFunction<ServerResponse> nestRouterFunction() {
    return nest(path("/user")
        , route(
            GET("/hello"), request -> ok().body(
                just("SOME RESPONSE"), String.class
            )
        ).andRoute(
            GET("/hi"), request -> ok().body(
                just("SOME RESPONSE HI"), String.class)
        ).andRoute(
            POST("/helloHi"), request -> ok().body(
                just("SOME RESPONE HELLO AND HI"), String.class)
        )
    );
  }
}