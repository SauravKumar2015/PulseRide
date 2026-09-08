package com.pulseride.matching.exception;

public class MatchNotFoundException
        extends RuntimeException {

    public MatchNotFoundException(
            String message) {

        super(message);
    }
}