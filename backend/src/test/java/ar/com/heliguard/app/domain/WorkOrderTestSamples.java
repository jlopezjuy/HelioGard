package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WorkOrderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static WorkOrder getWorkOrderSample1() {
        return new WorkOrder().id(1L).referenceCode("referenceCode1").title("title1");
    }

    public static WorkOrder getWorkOrderSample2() {
        return new WorkOrder().id(2L).referenceCode("referenceCode2").title("title2");
    }

    public static WorkOrder getWorkOrderRandomSampleGenerator() {
        return new WorkOrder()
            .id(longCount.incrementAndGet())
            .referenceCode(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString());
    }
}
