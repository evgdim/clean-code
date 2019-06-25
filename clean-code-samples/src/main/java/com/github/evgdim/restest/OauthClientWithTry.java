package com.github.evgdim.restest;

import io.vavr.control.Try;

public class OauthClientWithTry {
	private String tokenCache;
	
	public Try<String> getAccessToken(String clientKey, String clientSecret) {
		return Try.of(() -> "");
	}
	
	public Try<?> callService(String request, String clientKey, String clientSecret) {
		return Try.of(() -> tokenCache)
				.map(token -> httpCall(request, token))
				.recover(InvalidTokenExcetion.class, err -> httpCall(request, getToken(clientKey, clientSecret)));
	}

	private String getToken(String _2, String _3) {
		//get new token
		tokenCache = "new token";
		return null;
	}

	private Integer httpCall(String req, String token) {
		return 1;
	}
}

class InvalidTokenExcetion extends RuntimeException {
	
}