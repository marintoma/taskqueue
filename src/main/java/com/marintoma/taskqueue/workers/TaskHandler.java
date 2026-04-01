package com.marintoma.taskqueue.workers;

import com.marintoma.taskqueue.entities.TaskExecution;
import com.marintoma.taskqueue.enums.TaskType;
import com.marintoma.taskqueue.exceptions.TaskHandlerException;

public interface TaskHandler {

    /**
     * Returns the task handlers supported type.
     *
     * @return what type of task does this handler support.
     */
    TaskType getSupportedType();

    /**
     * Handles the specified task.
     *
     * @param execution the execution to be handled
     * @throws TaskHandlerException if the handler fails to execute the task
     */
    void handle(TaskExecution execution) throws TaskHandlerException;
}
