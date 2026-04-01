package com.marintoma.taskqueue.exceptions;

public class ExecutorTaskException extends TaskHandlerException {
    public ExecutorTaskException(String message) {
        super(message);
    }

    public ExecutorTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
