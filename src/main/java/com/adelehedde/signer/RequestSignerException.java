package com.adelehedde.signer;

public class RequestSignerException extends RuntimeException {

    public RequestSignerException(String message) {
        super(message);
    }

    public RequestSignerException(String message, Throwable cause) {
        super(message, cause);
    }
}
