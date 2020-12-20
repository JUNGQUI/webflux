package com.practice.jk.webflux.ob;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ObTest {

	@Test
	void iteratorTest() {
		// Iterable 을 상속받아 사용함
		List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		// 불편함, Iterable 을 보면 '반복' 하는 method 가 존재
		Iterable<Integer> integerIterableWithImplement = new Iterable<Integer>() {
			@Override public Iterator<Integer> iterator() {
				return null;
			}
		};
		// 하나밖에 없네? 람다식으로, 그리고 iterator 를 생성해서 반환
		Iterable<Integer> integerIterableWithImplementAndLambda = () -> new Iterator<Integer>() {
			int i = 0;
			final static int MAX = 10;

			@Override public boolean hasNext() {
				return i < MAX;
			}

			@Override public Integer next() {
				return ++i;
			}
		};

		Iterator<Integer> integerIterable = integerIterableWithImplementAndLambda.iterator();

		for (int i = 0; i < 10; i++) {
			Assertions.assertEquals(integerList.get(i), integerIterable.next());
		}
	}
}