package io.openjob.worker.task;

import java.util.List;

/**
 * @author stelin <swoft@qq.com>
 * @since 1.0.0
 */
public class ContainerTaskStatusConsumer<T> extends BaseConsumer<T> {

    public ContainerTaskStatusConsumer(Long id,
                                       Integer handlerCoreThreadNum,
                                       Integer handlerMaxThreadNum,
                                       String handlerThreadName,
                                       Integer pollSize,
                                       String pollThreadName,
                                       TaskQueue<T> queues) {
        super(id, handlerCoreThreadNum, handlerMaxThreadNum, handlerThreadName, pollSize, pollThreadName, queues);
    }

    @Override
    public void consume(Long id, List<T> tasks) {

    }
}
