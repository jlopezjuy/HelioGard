package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LossCauseEstimateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static LossCauseEstimate getLossCauseEstimateSample1() {
        return new LossCauseEstimate().id(1L).method("method1");
    }

    public static LossCauseEstimate getLossCauseEstimateSample2() {
        return new LossCauseEstimate().id(2L).method("method2");
    }

    public static LossCauseEstimate getLossCauseEstimateRandomSampleGenerator() {
        return new LossCauseEstimate().id(longCount.incrementAndGet()).method(UUID.randomUUID().toString());
    }
}
