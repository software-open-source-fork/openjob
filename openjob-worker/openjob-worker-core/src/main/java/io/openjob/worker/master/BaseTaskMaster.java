package io.openjob.worker.master;

import akka.actor.ActorContext;
import io.openjob.common.constant.AkkaConstant;
import io.openjob.common.constant.TaskStatusEnum;
import io.openjob.worker.constant.WorkerAkkaConstant;
import io.openjob.worker.dao.TaskDAO;
import io.openjob.worker.dto.JobInstanceDTO;
import io.openjob.worker.entity.Task;
import io.openjob.worker.request.ContainerBatchTaskStatusRequest;
import io.openjob.worker.request.ContainerTaskStatusRequest;
import io.openjob.worker.request.MasterStartContainerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author stelin <swoft@qq.com>
 * @since 1.0.0
 */
public abstract class BaseTaskMaster implements TaskMaster {

    protected AtomicLong taskIdGenerator = new AtomicLong(0);

    protected AtomicLong circleIdGenerator = new AtomicLong(0);

    protected JobInstanceDTO jobInstanceDTO;
    protected ActorContext actorContext;
    protected String localWorkerAddress;
    protected String localContainerPath;

    /**
     * Task dao.
     */
    protected TaskDAO taskDAO = TaskDAO.INSTANCE;

    public BaseTaskMaster(JobInstanceDTO jobInstanceDTO, ActorContext actorContext) {
        this.jobInstanceDTO = jobInstanceDTO;
        this.actorContext = actorContext;
        this.localWorkerAddress = actorContext.provider().addressString();
        this.localContainerPath = actorContext.provider().getDefaultAddress().toString() + WorkerAkkaConstant.PATH_TASK_CONTAINER;
    }

    protected void init() {

    }

    @Override
    public void completeTask() {
        Long jobId = this.jobInstanceDTO.getJobId();
        Long instanceId = this.jobInstanceDTO.getJobInstanceId();
        // Task complete.
        if (this.isTaskComplete(jobId, this.circleIdGenerator.get())) {
            System.out.printf("task complete jobId=%s instanceId=%s%n", jobId, instanceId);
        }
    }

    @Override
    public void updateStatus(ContainerBatchTaskStatusRequest batchRequest) {
        // Update list
        List<Task> updateList = new ArrayList<>();
        for (ContainerTaskStatusRequest statusRequest : batchRequest.getTaskStatusList()) {
            String taskUniqueId = statusRequest.getTaskUniqueId();
            updateList.add(new Task(taskUniqueId, statusRequest.getStatus()));
        }

        // Group by status
        Map<Integer, List<Task>> groupList = updateList.stream().collect(Collectors.groupingBy(Task::getStatus));

        // Update by group
        for (Map.Entry<Integer, List<Task>> entry : groupList.entrySet()) {
            taskDAO.batchUpdateStatusByTaskId(entry.getValue(), entry.getKey());
        }

        this.completeTask();
    }

    protected Long acquireTaskId() {
        return taskIdGenerator.getAndIncrement();
    }

    protected MasterStartContainerRequest getMasterStartContainerRequest() {
        MasterStartContainerRequest startReq = new MasterStartContainerRequest();
        startReq.setJobId(this.jobInstanceDTO.getJobId());
        startReq.setJobInstanceId(this.jobInstanceDTO.getJobInstanceId());
        startReq.setTaskId(this.acquireTaskId());
        startReq.setJobParams(this.jobInstanceDTO.getJobParams());
        startReq.setExecuteType(this.jobInstanceDTO.getExecuteType());
        startReq.setWorkflowId(this.jobInstanceDTO.getWorkflowId());
        startReq.setProcessorType(this.jobInstanceDTO.getProcessorType());
        startReq.setProcessorInfo(this.jobInstanceDTO.getProcessorInfo());
        startReq.setFailRetryInterval(this.jobInstanceDTO.getFailRetryInterval());
        startReq.setFailRetryTimes(this.jobInstanceDTO.getFailRetryTimes());
        startReq.setTimeExpression(this.jobInstanceDTO.getTimeExpression());
        startReq.setTimeExpressionType(this.jobInstanceDTO.getTimeExpressionType());
        startReq.setConcurrency(this.jobInstanceDTO.getConcurrency());
        startReq.setMasterAkkaPath(this.localContainerPath);
        startReq.setWorkerAddresses(this.jobInstanceDTO.getWorkerAddresses());
        startReq.setMasterAkkaPath(String.format("%s%s", this.localWorkerAddress, AkkaConstant.WORKER_PATH_TASK_MASTER));
        return startReq;
    }

    protected Task convertToTask(MasterStartContainerRequest startRequest) {
        Task task = new Task();
        task.setJobId(startRequest.getJobId());
        task.setInstanceId(startRequest.getJobInstanceId());
        task.setCircleId(startRequest.getCircleId());
        task.setTaskId(startRequest.getTaskUniqueId());
        task.setTaskParentId(startRequest.getParentTaskUniqueId());
        task.setTaskName(startRequest.getTaskName());
        task.setStatus(TaskStatusEnum.INIT.getStatus());
        task.setWorkerAddress(this.localWorkerAddress);
        return task;
    }

    protected Boolean isTaskComplete(Long instanceId, Long circleId) {
        return taskDAO.countTask(instanceId, circleId, TaskStatusEnum.NON_FINISH_LIST) == 0;
    }
}
