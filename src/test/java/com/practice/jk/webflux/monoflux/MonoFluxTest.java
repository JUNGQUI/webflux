package com.practice.jk.webflux.monoflux;

import com.practice.jk.webflux.monoflux.service.MonoFluxService;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

@SpringBootTest
class MonoFluxTest {

	@Autowired
	private MonoFluxService service;

	@Test
	void monoFluxTest() {
		MonoFlux.simpleFlux();
	}

	@Test
	void publishByFlux() {
		MonoFlux.publishByFlux();
	}

	@Test
	void multiThread() {
		try {
			StepVerifier.create(service.multiMono())
					.expectNextMatches(Predicate.isEqual("long\nsmall"))
					.verifyComplete();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
}