package com.practice.jk.webflux.ob;

import java.util.Observable;

// 참고 : java 9 부터 Observable 은 deprecated 되었다.
public class Ob extends Observable implements Runnable {
	@Override
	public void run() {
		for (int i = 0; i <= 10; i++) {
			setChanged();
			notifyObservers(i);
		}
	}
}
