package com.marintoma.taskqueue.workers.handlers;

import com.marintoma.taskqueue.dtos.ExecutorConfig;
import com.marintoma.taskqueue.entities.TaskExecution;
import com.marintoma.taskqueue.enums.TaskType;
import com.marintoma.taskqueue.exceptions.ExecutableTaskException;
import com.marintoma.taskqueue.exceptions.ExecutorTaskException;
import com.marintoma.taskqueue.exceptions.TaskHandlerException;
import com.marintoma.taskqueue.workers.ExecutableTask;
import com.marintoma.taskqueue.workers.TaskHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExecutorHandler implements TaskHandler {

    @Override
    public TaskType getSupportedType() {
        return TaskType.EXECUTOR;
    }

    @Override
    public void handle(TaskExecution execution) throws TaskHandlerException {
        ExecutorConfig config = execution.getDefinition().getExecutorConfig();

        if (config == null) {
            throw new IllegalStateException("ExecutorConfig is missing for execution "
                    + execution.getId());
        }

        log.info("Executing handler class {} for execution {}",
                config.handlerClass(), execution.getId());

        Class<?> handlerClass = loadHandlerClass(config.handlerClass(), execution);
        ExecutableTask task = instantiateTask(handlerClass, config.handlerClass(), execution);

        try {
            task.execute(execution.getPayload());
            log.info("Executor task {} completed successfully for execution {}",
                    config.handlerClass(), execution.getId());
        } catch (ExecutableTaskException e) {
            log.error("Executor task {} failed for execution {}: {}",
                    config.handlerClass(), execution.getId(), e.getMessage());
            throw new TaskHandlerException(
                    "Handler " + config.handlerClass() + " threw an exception: "
                            + e.getMessage(), e);
        }
    }

    private Class<?> loadHandlerClass(String className, TaskExecution execution) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("Handler class {} not found on classpath for execution {}",
                    className, execution.getId());
            throw new ExecutorTaskException(
                    "Handler class not found on classpath: " + className, e);
        }
    }

    private ExecutableTask instantiateTask(Class<?> handlerClass,
                                           String className, TaskExecution execution) {
        try {
            Object instance = handlerClass.getDeclaredConstructor().newInstance();
            if (!(instance instanceof ExecutableTask task)) {
                log.error("Class {} does not implement ExecutableTask for execution {}",
                        className, execution.getId());
                throw new ExecutorTaskException(
                        className + " must implement ExecutableTask");
            }
            return task;
        } catch (ExecutorTaskException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to instantiate handler class {} for execution {}: {}",
                    className, execution.getId(), e.getMessage());
            throw new ExecutorTaskException(
                    "Failed to instantiate handler class: " + className, e);
        }
    }
}
