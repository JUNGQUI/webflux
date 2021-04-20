package com.practice.jk.webflux.async;

import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JKCompletableFutureTest {

  @Autowired
  private JKCompletableFuture jkCompletableFuture;

  @Test
  void syncJoinTest() {
    List<JKCompletableObject> jkCompletableObjectList = Arrays.asList(
        new JKCompletableObject("id1", "password1"),
        new JKCompletableObject("id2", "password2"),
        new JKCompletableObject("id3", "password3"),
        new JKCompletableObject("id4", "password4"),
        new JKCompletableObject("id5", "password5")
    );
    List<String> result = jkCompletableFuture.syncJoinIdAndPassword(jkCompletableObjectList);

    result.forEach(
        res -> {
          System.out.println(res);
          Assertions.assertThat(res)
              .contains("id")
              .contains("|")
              .contains("password");
        }
    );
  }

  @Test
  void asyncJoinTest() {
    List<JKCompletableObject> jkCompletableObjectList = Arrays.asList(
        new JKCompletableObject("id1", "password1"),
        new JKCompletableObject("id2", "password2"),
        new JKCompletableObject("id3", "password3"),
        new JKCompletableObject("id4", "password4"),
        new JKCompletableObject("id5", "password5")
    );

    List<String> result = jkCompletableFuture.asyncJoinIdAndPassword(jkCompletableObjectList);

    result.forEach(
        res -> {
          System.out.println(res);
          Assertions.assertThat(res)
              .contains("id")
              .contains("|")
              .contains("password");
        }
    );
  }

}