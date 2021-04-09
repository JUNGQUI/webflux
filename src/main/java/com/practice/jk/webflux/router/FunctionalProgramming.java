package com.practice.jk.webflux.router;

import java.util.function.Function;

public class FunctionalProgramming {

  public String functional1(Function<String, String> function, String someText) {
    return function.apply(someText);
  }
}
