package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SolarStringTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static SolarString getSolarStringSample1() {
        return new SolarString().id(1L).stringCode("stringCode1").modulesInSeries(1);
    }

    public static SolarString getSolarStringSample2() {
        return new SolarString().id(2L).stringCode("stringCode2").modulesInSeries(2);
    }

    public static SolarString getSolarStringRandomSampleGenerator() {
        return new SolarString()
            .id(longCount.incrementAndGet())
            .stringCode(UUID.randomUUID().toString())
            .modulesInSeries(intCount.incrementAndGet());
    }
}
