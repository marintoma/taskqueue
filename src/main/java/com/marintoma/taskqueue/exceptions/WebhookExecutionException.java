package com.marintoma.taskqueue.exceptions;

import lombok.Getter;

@Getter
public class WebhookExecutionException extends TaskHandlerException {

    private final int statusCode;
    private final String responseBody;

    public WebhookExecutionException(int statusCode, String responseBody) {
        super("Webhook failed with status " + statusCode + ": " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public WebhookExecutionException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.responseBody = null;
    }
}
