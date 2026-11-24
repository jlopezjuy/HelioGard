package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MaintenanceInterventionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static MaintenanceIntervention getMaintenanceInterventionSample1() {
        return new MaintenanceIntervention().id(1L).currencyCode("currencyCode1");
    }

    public static MaintenanceIntervention getMaintenanceInterventionSample2() {
        return new MaintenanceIntervention().id(2L).currencyCode("currencyCode2");
    }

    public static MaintenanceIntervention getMaintenanceInterventionRandomSampleGenerator() {
        return new MaintenanceIntervention().id(longCount.incrementAndGet()).currencyCode(UUID.randomUUID().toString());
    }
}
