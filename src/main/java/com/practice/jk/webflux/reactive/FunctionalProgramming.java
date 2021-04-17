package com.practice.jk.webflux.reactive;

import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;

@UtilityClass
public class FunctionalProgramming {

  public String exampleFunction(String someText) {
    return someText + " this is imperative";
  }

  public Mono<String> searchAndAppend(List<String> needSearch) throws Exception {
    Thread.sleep(1000);
    return Mono.just(
        needSearch.stream()
            .filter(s -> s.startsWith("s"))
            .map(s -> s + " found")
        .collect(Collectors.joining(" | "))
    );
  }
}
