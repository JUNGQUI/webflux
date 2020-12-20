package com.practice.jk.webflux.ob;

import java.util.Iterator;

public class Ob {
	public static void main(String[] args) {
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
	}
}
