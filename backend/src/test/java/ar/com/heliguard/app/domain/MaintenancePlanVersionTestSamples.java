package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MaintenancePlanVersionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static MaintenancePlanVersion getMaintenancePlanVersionSample1() {
        return new MaintenancePlanVersion()
            .id(1L)
            .versionCode("versionCode1")
            .currencyCode("currencyCode1")
            .termsReference("termsReference1");
    }

    public static MaintenancePlanVersion getMaintenancePlanVersionSample2() {
        return new MaintenancePlanVersion()
            .id(2L)
            .versionCode("versionCode2")
            .currencyCode("currencyCode2")
            .termsReference("termsReference2");
    }

    public static MaintenancePlanVersion getMaintenancePlanVersionRandomSampleGenerator() {
        return new MaintenancePlanVersion()
            .id(longCount.incrementAndGet())
            .versionCode(UUID.randomUUID().toString())
            .currencyCode(UUID.randomUUID().toString())
            .termsReference(UUID.randomUUID().toString());
    }
}
