package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MaintenancePlanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static MaintenancePlan getMaintenancePlanSample1() {
        return new MaintenancePlan().id(1L).name("name1");
    }

    public static MaintenancePlan getMaintenancePlanSample2() {
        return new MaintenancePlan().id(2L).name("name2");
    }

    public static MaintenancePlan getMaintenancePlanRandomSampleGenerator() {
        return new MaintenancePlan().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
