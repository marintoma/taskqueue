package com.marintoma.taskqueue.workers;

import com.marintoma.taskqueue.enums.TaskType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskHandlerRegistry {

    private final List<TaskHandler> handlers;
    private Map<TaskType, TaskHandler> handlerMap;

    @PostConstruct
    public void init() {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(TaskHandler::getSupportedType,
                        Function.identity()));
        log.info("Registered task handlers:  {}", handlerMap.keySet());
    }

    public TaskHandler getHandler(TaskType type) {
        TaskHandler handler = handlerMap.get(type);
        if (handler == null) {
            throw new IllegalStateException("No handler registered for task type " + type);
        }
        return handler;
    }

}
