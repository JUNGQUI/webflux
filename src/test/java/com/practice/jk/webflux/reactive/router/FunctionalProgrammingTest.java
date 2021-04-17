package com.practice.jk.webflux.reactive.router;

import com.practice.jk.webflux.reactive.FunctionalProgramming;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
class FunctionalProgrammingTest {

  @Test
  public void test1() {
    String original = "original";
    String appendedText = " this is imperative";

    String imperativeResult = FunctionalProgramming.exampleFunction(original);
    String declarativeResult = String.valueOf(Mono.just(original)
        .map(FunctionalProgramming::exampleFunction)
        .block());

    Assertions.assertEquals(original + appendedText, imperativeResult);
    Assertions.assertEquals(original + appendedText, declarativeResult);
  }
}