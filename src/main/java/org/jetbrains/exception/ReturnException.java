package org.jetbrains.exception;

public class ReturnException extends RuntimeException {
    public final int value;

    public ReturnException(int value) {
        this.value = value;
    }
}
