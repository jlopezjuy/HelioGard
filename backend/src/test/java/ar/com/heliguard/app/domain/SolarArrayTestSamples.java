package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SolarArrayTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static SolarArray getSolarArraySample1() {
        return new SolarArray().id(1L).name("name1");
    }

    public static SolarArray getSolarArraySample2() {
        return new SolarArray().id(2L).name("name2");
    }

    public static SolarArray getSolarArrayRandomSampleGenerator() {
        return new SolarArray().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
