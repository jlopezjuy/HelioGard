package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WorkOrderAssignmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static WorkOrderAssignment getWorkOrderAssignmentSample1() {
        return new WorkOrderAssignment().id(1L).notes("notes1");
    }

    public static WorkOrderAssignment getWorkOrderAssignmentSample2() {
        return new WorkOrderAssignment().id(2L).notes("notes2");
    }

    public static WorkOrderAssignment getWorkOrderAssignmentRandomSampleGenerator() {
        return new WorkOrderAssignment().id(longCount.incrementAndGet()).notes(UUID.randomUUID().toString());
    }
}
