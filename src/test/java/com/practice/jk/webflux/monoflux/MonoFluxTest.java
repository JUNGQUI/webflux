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

	// TODO 해당 request 에 대해 multi 로 신청하는 것 처럼 변경
	// Verify 중에 있다고 함
	@Test
	void publishByFlux() {
		MonoFlux.publishByFlux();
		MonoFlux.publishByFlux();
		MonoFlux.publishByFlux();
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