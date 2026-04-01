package com.marintoma.taskqueue.workers;

import com.marintoma.taskqueue.exceptions.ExecutableTaskException;

import java.util.Map;

public interface ExecutableTask {

    /**
     * Executes the task.
     *
     * @param payload the task to be executed with metadata.
     * @throws ExecutableTaskException if the task fails while execution
     */
    void execute(Map<String, Object> payload) throws ExecutableTaskException;
}
