package io.openjob.server.scheduler.wheel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author stelin <swoft@qq.com>
 * @since 1.0.0
 */
@Component
public class WheelManager {
    private final SchedulerWheel schedulerWheel;
    private final WorkflowWheel workflowWheel;

    @Autowired
    public WheelManager(SchedulerWheel schedulerWheel, WorkflowWheel workflowWheel) {
        this.schedulerWheel = schedulerWheel;
        this.workflowWheel = workflowWheel;
    }

    /**
     * Start
     */
    public void start() {
        // Scheduler wheel
        this.schedulerWheel.start();

        // Workflow wheel
        this.workflowWheel.start();
    }

    /**
     * Remove by slot id.
     *
     * @param slotsIds slot ids.
     */
    public void removeBySlotsId(Set<Long> slotsIds) {
        // Scheduler wheel
        this.schedulerWheel.removeBySlotsId(slotsIds);

        // Workflow wheel
        this.workflowWheel.removeBySlotsId(slotsIds);
    }

    /**
     * Stop
     */
    public void stop() {
        // Scheduler wheel
        this.schedulerWheel.stop();

        // Workflow wheel
        this.workflowWheel.stop();
    }
}
