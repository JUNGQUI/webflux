package com.practice.jk.webflux.monoflux;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MonoFluxTest {

	@Test
	void monoFluxTest() {
		MonoFlux.simpleFlux();
	}

	@Test
	void publishByFlux() {
		MonoFlux.publishByFlux();
	}
}