package com.zerox80.riotapi.client;

/**
 * Runtime exception thrown when a Riot API request fails.
 * 
 * <p>This exception is used to encapsulate errors that occur during HTTP requests
 * to the Riot Games API, including network failures, parsing errors, and API error responses.</p>
 * 
 * @author zerox80
 * @version 2.0
 * @since 2.0
 */
public class RiotApiRequestException extends RuntimeException {

    /**
     * Constructs a new RiotApiRequestException with the specified detail message.
     * 
     * @param message The detail message explaining the reason for the exception
     */
    public RiotApiRequestException(String message) {
        super(message);
    }

    /**
     * Constructs a new RiotApiRequestException with the specified detail message and cause.
     * 
     * @param message The detail message explaining the reason for the exception
     * @param cause The underlying cause of the exception
     */
    public RiotApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
