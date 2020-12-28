package com.practice.jk.webflux.monoflux.service;

import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {

  public CompletableFuture<String> asyncService(String request) {
    return CompletableFuture.completedFuture("AsyncService " + request);
  }
}
