package com.marintoma.taskqueue.workers.handlers;

import com.marintoma.taskqueue.dtos.WebhookConfig;
import com.marintoma.taskqueue.entities.TaskExecution;
import com.marintoma.taskqueue.enums.TaskType;
import com.marintoma.taskqueue.exceptions.TaskHandlerException;
import com.marintoma.taskqueue.exceptions.WebhookExecutionException;
import com.marintoma.taskqueue.workers.TaskHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpWebhookHandler implements TaskHandler {

    private final HttpClient httpClient;

    @Override
    public TaskType getSupportedType() {
        return TaskType.HTTP_WEBHOOK;
    }

    @Override
    public void handle(TaskExecution execution) throws TaskHandlerException {
        WebhookConfig config = execution.getDefinition().getWebhookConfig();

        if (config == null) {
            throw new IllegalStateException("WebhookConfig is missing for execution "
                    + execution.getId());
        }

        log.info("Executing webhook for execution {} — {} {}",
                execution.getId(), config.method(), config.url());

        HttpRequest request = buildRequest(config,
                execution.getDefinition().getTimeoutMs());

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            log.error("Network error executing webhook for execution {}: {}",
                    execution.getId(), e.getMessage());
            throw new TaskHandlerException(
                    "Network error calling webhook: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Webhook execution interrupted for execution {}",
                    execution.getId());
            throw new TaskHandlerException(
                    "Webhook execution was interrupted", e);
        }

        List<Integer> successCodes = config.successStatusCodes() != null
                ? config.successStatusCodes()
                : List.of(200, 201, 202);

        log.debug("Webhook response for execution {} — status {} body: {}",
                execution.getId(), response.statusCode(), response.body());

        if (!successCodes.contains(response.statusCode())) {
            log.error("Webhook failed for execution {} — expected one of {} but got {} — response: {}",
                    execution.getId(), successCodes, response.statusCode(), response.body());
            throw new WebhookExecutionException(response.statusCode(), response.body());
        }

        log.info("Webhook succeeded for execution {} — status {}",
                execution.getId(), response.statusCode());
    }

    private HttpRequest buildRequest(WebhookConfig config, long timeoutMs) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(config.url()))
                .timeout(Duration.ofMillis(timeoutMs));

        if (config.headers() != null) {
            config.headers().forEach(builder::header);
        }

        String body = config.body() != null ? config.body() : "";
        builder.method(config.method().name(),
                HttpRequest.BodyPublishers.ofString(body));

        return builder.build();
    }
}
