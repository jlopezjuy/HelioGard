package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WorkOrderTaskTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static WorkOrderTask getWorkOrderTaskSample1() {
        return new WorkOrderTask().id(1L).description("description1").sortOrder(1);
    }

    public static WorkOrderTask getWorkOrderTaskSample2() {
        return new WorkOrderTask().id(2L).description("description2").sortOrder(2);
    }

    public static WorkOrderTask getWorkOrderTaskRandomSampleGenerator() {
        return new WorkOrderTask()
            .id(longCount.incrementAndGet())
            .description(UUID.randomUUID().toString())
            .sortOrder(intCount.incrementAndGet());
    }
}
