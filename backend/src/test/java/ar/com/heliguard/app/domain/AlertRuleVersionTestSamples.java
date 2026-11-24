package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AlertRuleVersionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static AlertRuleVersion getAlertRuleVersionSample1() {
        return new AlertRuleVersion()
            .id(1L)
            .versionCode("versionCode1")
            .metricUnitCode("metricUnitCode1")
            .windowSeconds(1)
            .cooldownSeconds(1);
    }

    public static AlertRuleVersion getAlertRuleVersionSample2() {
        return new AlertRuleVersion()
            .id(2L)
            .versionCode("versionCode2")
            .metricUnitCode("metricUnitCode2")
            .windowSeconds(2)
            .cooldownSeconds(2);
    }

    public static AlertRuleVersion getAlertRuleVersionRandomSampleGenerator() {
        return new AlertRuleVersion()
            .id(longCount.incrementAndGet())
            .versionCode(UUID.randomUUID().toString())
            .metricUnitCode(UUID.randomUUID().toString())
            .windowSeconds(intCount.incrementAndGet())
            .cooldownSeconds(intCount.incrementAndGet());
    }
}
