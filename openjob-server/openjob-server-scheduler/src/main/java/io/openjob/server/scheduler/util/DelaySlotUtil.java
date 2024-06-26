package io.openjob.server.scheduler.util;

import io.openjob.server.common.ClusterContext;
import io.openjob.server.common.util.CrcUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author stelin swoft@qq.com
 * @since 1.0.0
 */
@Slf4j
public class DelaySlotUtil {

    /**
     * Get current zset slots.
     *
     * @return List
     */
    public static List<Long> getCurrentZsetSlots() {
        int maxSlot = ClusterContext.getSystem().getMaxSlot();
        int delayZsetMaxSlot = ClusterContext.getSystem().getDelayZsetSlot();
        return getCurrentSlots(maxSlot, delayZsetMaxSlot);
    }

    /**
     * Get current zset slots.
     *
     * @return List
     */
    public static List<Long> getFailCurrentZsetSlots() {
        int maxSlot = ClusterContext.getSystem().getMaxSlot();
        int delayFailZsetMaxSlot = ClusterContext.getSystem().getDelayFailZsetSlot();
        return getCurrentSlots(maxSlot, delayFailZsetMaxSlot);
    }

    /**
     * Get current add list slots.
     *
     * @return List
     */
    public static List<Long> getCurrentAddListSlots() {
        int maxSlot = ClusterContext.getSystem().getMaxSlot();
        int delayListMaxSlot = ClusterContext.getSystem().getDelayAddListSlot();
        return getCurrentSlots(maxSlot, delayListMaxSlot);
    }

    /**
     * Get current status list slots.
     *
     * @return List
     */
    public static List<Long> getCurrentStatusListSlots() {
        int maxSlot = ClusterContext.getSystem().getMaxSlot();
        int delayStatusMaxSlot = ClusterContext.getSystem().getDelayStatusListSlot();
        return getCurrentSlots(maxSlot, delayStatusMaxSlot);
    }

    /**
     * Get current delete list slots.
     *
     * @return List
     */
    public static List<Long> getCurrentDeleteListSlots() {
        int maxSlot = ClusterContext.getSystem().getMaxSlot();
        int delayDeleteMaxSlot = ClusterContext.getSystem().getDelayDeleteListSlot();
        return getCurrentSlots(maxSlot, delayDeleteMaxSlot);
    }

    /**
     * Get add list slot id.
     *
     * @param key key
     * @return Long
     */
    public static Long getAddListSlotId(String key) {
        int delayListMaxSlot = ClusterContext.getSystem().getDelayAddListSlot();
        return (long) CrcUtil.crc16(key.getBytes()) % delayListMaxSlot + 1;
    }

    /**
     * Get status list slot id.
     *
     * @param key key
     * @return Long
     */
    public static Long getStatusListSlotId(String key) {
        int delayListMaxSlot = ClusterContext.getSystem().getDelayStatusListSlot();
        return (long) CrcUtil.crc16(key.getBytes()) % delayListMaxSlot + 1;
    }

    /**
     * Get zset slot id.
     *
     * @param key key
     * @return Long
     */
    public static Long getZsetSlotId(String key) {
        int delayZsetMaxSlot = ClusterContext.getSystem().getDelayZsetSlot();
        return (long) CrcUtil.crc16(key.getBytes()) % delayZsetMaxSlot + 1;
    }

    /**
     * Get zset slot id.
     *
     * @param key key
     * @return Long
     */
    public static Long getFailZsetSlotId(String key) {
        int delayFailZsetMaxSlot = ClusterContext.getSystem().getDelayFailZsetSlot();
        return (long) CrcUtil.crc16(key.getBytes()) % delayFailZsetMaxSlot + 1;
    }

    /**
     * Get current slots.
     *
     * @param maxSlot     max slot
     * @param currentSlot current slot
     * @return List
     */
    public static List<Long> getCurrentSlots(Integer maxSlot, Integer currentSlot) {
        Set<Long> slots = new HashSet<>();
        if (currentSlot > maxSlot) {
            log.error("Current slot must less than max slot.");
            return new ArrayList<>(slots);
        }

        Set<Long> nodeSlots = ClusterContext.getCurrentSlots();
        for (int i = 1; i < currentSlot + 1; i++) {
            if (nodeSlots.contains((long) i)) {
                slots.add((long) i);
            }
        }
        return new ArrayList<>(slots);
    }
}
