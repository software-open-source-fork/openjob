package io.openjob.worker.processor;

import akka.actor.ActorSelection;
import com.google.common.collect.Lists;
import io.openjob.common.response.WorkerResponse;
import io.openjob.common.util.FutureUtil;
import io.openjob.common.util.KryoUtil;
import io.openjob.worker.OpenjobWorker;
import io.openjob.worker.container.TaskContainerFactory;
import io.openjob.worker.context.JobContext;
import io.openjob.worker.request.ProcessorMapTaskRequest;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stelin <swoft@qq.com>
 * @since 1.0.0
 */
public interface MapProcessor extends BaseProcessor {
    default ProcessResult map(List<? extends Object> tasks, String taskName) {
        ProcessResult result = new ProcessResult(false);

        if (CollectionUtils.isEmpty(tasks)) {
            return result;
        }

        JobContext jobContext = TaskContainerFactory.getPool().getJobContext();
        ActorSelection masterSelection = OpenjobWorker.getActorSystem().actorSelection(jobContext.getMasterActorPath());

        int batchSize = 100;
        List<List<Object>> splitTasks = Lists.partition((List<Object>) tasks, batchSize);
        splitTasks.forEach((batchTasks) -> {
            ProcessorMapTaskRequest mapTaskRequest = new ProcessorMapTaskRequest();
            mapTaskRequest.setJobId(jobContext.getJobId());
            mapTaskRequest.setJobInstanceId(jobContext.getJobInstanceId());
            mapTaskRequest.setTaskId(jobContext.getTaskId());
            mapTaskRequest.setTaskName(taskName);

            List<byte[]> byteList = new ArrayList<>();
            batchTasks.forEach(t -> byteList.add(KryoUtil.serialize(t)));
            mapTaskRequest.setTasks(byteList);

            try {
                WorkerResponse workerResponse = FutureUtil.mustAsk(masterSelection, mapTaskRequest, WorkerResponse.class, 10L);
                System.out.println(workerResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return result;
    }

    @Override
    default void stop(JobContext context) {

    }
}
