package com.practice.jk.webflux.async;

import lombok.Data;

@Data
public class JKCompletableObject {
	private String id;
	private String password;

	public JKCompletableObject(String id, String password) {
		this.id = id;
		this.password = password;
	}

	public static JKCompletableObject saveObject(String id, String password) throws InterruptedException {
		// 계정을 DB 에 저장하는데 1초 소요된다고 가정한다.
		Thread.sleep(1000);
		return new JKCompletableObject(id, password);
	}
}
