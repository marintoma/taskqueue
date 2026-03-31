package com.marintoma.taskqueue.dtos;

import com.marintoma.taskqueue.enums.HttpMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;
import java.util.Map;

public record WebhookConfig(

        @NotBlank(message = "URL is required")
        @Pattern(regexp = "https?://.*", message = "URL must start with http:// or https://")
        String url,

        @NotBlank(message = "HTTP method is required")
        HttpMethod method,

        Map<String, String> headers,
        String body,
        List<Integer> successStatusCodes
) {
}
