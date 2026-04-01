package com.marintoma.taskqueue.exceptions;

public class ExecutableTaskException extends RuntimeException {
    public ExecutableTaskException(String message) {
        super(message);
    }
    public ExecutableTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
