package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TariffPlanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TariffPlan getTariffPlanSample1() {
        return new TariffPlan().id(1L).name("name1").currencyCode("currencyCode1").distributor("distributor1");
    }

    public static TariffPlan getTariffPlanSample2() {
        return new TariffPlan().id(2L).name("name2").currencyCode("currencyCode2").distributor("distributor2");
    }

    public static TariffPlan getTariffPlanRandomSampleGenerator() {
        return new TariffPlan()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .currencyCode(UUID.randomUUID().toString())
            .distributor(UUID.randomUUID().toString());
    }
}
