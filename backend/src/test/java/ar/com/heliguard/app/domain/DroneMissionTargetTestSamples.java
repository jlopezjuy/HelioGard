package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DroneMissionTargetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static DroneMissionTarget getDroneMissionTargetSample1() {
        return new DroneMissionTarget().id(1L).targetCode("targetCode1").priority(1).notes("notes1");
    }

    public static DroneMissionTarget getDroneMissionTargetSample2() {
        return new DroneMissionTarget().id(2L).targetCode("targetCode2").priority(2).notes("notes2");
    }

    public static DroneMissionTarget getDroneMissionTargetRandomSampleGenerator() {
        return new DroneMissionTarget()
            .id(longCount.incrementAndGet())
            .targetCode(UUID.randomUUID().toString())
            .priority(intCount.incrementAndGet())
            .notes(UUID.randomUUID().toString());
    }
}
