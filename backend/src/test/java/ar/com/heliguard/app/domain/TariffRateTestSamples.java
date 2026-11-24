package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TariffRateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TariffRate getTariffRateSample1() {
        return new TariffRate().id(1L);
    }

    public static TariffRate getTariffRateSample2() {
        return new TariffRate().id(2L);
    }

    public static TariffRate getTariffRateRandomSampleGenerator() {
        return new TariffRate().id(longCount.incrementAndGet());
    }
}
