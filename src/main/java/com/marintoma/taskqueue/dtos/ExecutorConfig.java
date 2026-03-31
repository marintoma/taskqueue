package com.marintoma.taskqueue.dtos;

import jakarta.validation.constraints.NotBlank;

public record ExecutorConfig(

        @NotBlank(message = "Handler class is required")
        String handlerClass
) {
}
