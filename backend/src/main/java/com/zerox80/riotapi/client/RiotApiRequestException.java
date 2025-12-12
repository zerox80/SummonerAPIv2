// Package declaration: Defines that this exception belongs to the client package
package com.zerox80.riotapi.client;


/**
 * Custom runtime exception for errors occurring during Riot API requests.
 *
 * This exception is thrown when:
 * - API requests fail with error status codes
 * - Response parsing fails
 * - Network errors occur during communication with Riot API
 *
 * Extends RuntimeException to be an unchecked exception, so it doesn't need
 * to be explicitly declared in method signatures.
 */
public class RiotApiRequestException extends RuntimeException {

    /**
     * Constructs a new RiotApiRequestException with the specified error message.
     *
     * @param message The error message describing what went wrong
     */
    public RiotApiRequestException(String message) {
        // Call the parent RuntimeException constructor with the error message
        super(message);
    }

    /**
     * Constructs a new RiotApiRequestException with an error message and the original cause.
     * This enables exception chaining to preserve the full error context.
     *
     * @param message The error message describing what went wrong
     * @param cause The original exception that caused this error (exception chaining)
     */
    public RiotApiRequestException(String message, Throwable cause) {
        // Call the parent RuntimeException constructor with both message and cause
        // This preserves the full exception chain for debugging
        super(message, cause);
    }
}
