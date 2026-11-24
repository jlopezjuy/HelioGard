package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DroneMissionEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static DroneMissionEvent getDroneMissionEventSample1() {
        return new DroneMissionEvent().id(1L).eventCode("eventCode1");
    }

    public static DroneMissionEvent getDroneMissionEventSample2() {
        return new DroneMissionEvent().id(2L).eventCode("eventCode2");
    }

    public static DroneMissionEvent getDroneMissionEventRandomSampleGenerator() {
        return new DroneMissionEvent().id(longCount.incrementAndGet()).eventCode(UUID.randomUUID().toString());
    }
}
