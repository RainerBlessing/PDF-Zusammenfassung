package com.aimitjava;

public class ApplicationTerminationException extends RuntimeException {
    private final int exitCode;

    public ApplicationTerminationException(String message, int exitCode) {
        super(message);
        this.exitCode = exitCode;
    }

    public int getExitCode() {
        return exitCode;
    }
}