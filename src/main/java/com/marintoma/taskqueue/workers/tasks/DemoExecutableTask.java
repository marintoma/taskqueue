package com.marintoma.taskqueue.workers.tasks;

import com.marintoma.taskqueue.exceptions.ExecutableTaskException;
import com.marintoma.taskqueue.workers.ExecutableTask;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class DemoExecutableTask implements ExecutableTask {

    @Override
    public void execute(Map<String, Object> payload) throws ExecutableTaskException {

        log.info("DemoExecutableTask executing with payload: {}", payload);

        String message = (String) payload.get("message");
        if (message == null) {
            throw new ExecutableTaskException("Missing required field: message");
        }

        // simulate work
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExecutableTaskException("Task was interrupted", e);
        }

        log.info("DemoExecutableTask completed with message: {}", message);
    }

}

