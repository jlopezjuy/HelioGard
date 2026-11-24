package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PlanSubscriptionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static PlanSubscription getPlanSubscriptionSample1() {
        return new PlanSubscription().id(1L).currencyCode("currencyCode1");
    }

    public static PlanSubscription getPlanSubscriptionSample2() {
        return new PlanSubscription().id(2L).currencyCode("currencyCode2");
    }

    public static PlanSubscription getPlanSubscriptionRandomSampleGenerator() {
        return new PlanSubscription().id(longCount.incrementAndGet()).currencyCode(UUID.randomUUID().toString());
    }
}
