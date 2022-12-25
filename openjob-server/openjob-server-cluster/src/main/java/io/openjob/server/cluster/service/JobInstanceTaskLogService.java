package io.openjob.server.cluster.service;

import com.google.common.collect.Lists;
import io.openjob.common.constant.LogFieldConstant;
import io.openjob.common.request.WorkerJobInstanceTaskLogFieldRequest;
import io.openjob.common.request.WorkerJobInstanceTaskLogRequest;
import io.openjob.server.log.dao.LogDAO;
import io.openjob.server.log.dto.ProcessorLog;
import io.openjob.server.log.mapper.LogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author stelin <swoft@qq.com>
 * @since 1.0.0
 */
@Slf4j
@Service
public class JobInstanceTaskLogService {
    private final LogDAO logDAO;

    @Autowired
    public JobInstanceTaskLogService(LogDAO logDAO) {
        this.logDAO = logDAO;
    }

    /**
     * Handle instance log.
     *
     * @param logReq log request.
     */
    public void handleInstanceTaskLog(WorkerJobInstanceTaskLogRequest logReq) {
        List<ProcessorLog> processorLogList = logReq.getFieldList().stream().map(fields -> {
            // Field map.
            Map<String, List<WorkerJobInstanceTaskLogFieldRequest>> fieldMap = fields.stream()
                    .collect(Collectors.groupingBy(WorkerJobInstanceTaskLogFieldRequest::getName));

            // Task id.
            String taskId = Optional.ofNullable(fieldMap.get(LogFieldConstant.TASK_ID))
                    .orElseGet(() -> Lists.newArrayList(new WorkerJobInstanceTaskLogFieldRequest())).get(0).getValue();

            // Worker address.
            String workerAddress = Optional.ofNullable(fieldMap.get(LogFieldConstant.WORKER_ADDRESS))
                    .orElseGet(() -> Lists.newArrayList(new WorkerJobInstanceTaskLogFieldRequest())).get(0).getValue();

            // Time
            String timeStamp = Optional.ofNullable(fieldMap.get(LogFieldConstant.TIME_STAMP))
                    .orElseGet(() -> Lists.newArrayList(new WorkerJobInstanceTaskLogFieldRequest())).get(0).getValue();

            ProcessorLog processorLog = new ProcessorLog();
            processorLog.setTaskId(taskId);
            processorLog.setWorkerAddress(workerAddress);
            processorLog.setTime(Long.valueOf(timeStamp));
            processorLog.setFields(LogMapper.INSTANCE.toProcessorLogFieldList(fields));
            return processorLog;
        }).collect(Collectors.toList());

        try {
            logDAO.batchAdd(processorLogList);
        } catch (SQLException e) {
            log.error("Batch add task log failed!", e);
            throw new RuntimeException(e);
        }
    }
}
