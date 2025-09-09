package com.zerox80.riotapi.client;

public class RiotApiRequestException extends RuntimeException {

    public RiotApiRequestException(String message) {
        super(message);
    }

    public RiotApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
