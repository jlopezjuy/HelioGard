package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DroneMissionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static DroneMission getDroneMissionSample1() {
        return new DroneMission().id(1L).missionCode("missionCode1");
    }

    public static DroneMission getDroneMissionSample2() {
        return new DroneMission().id(2L).missionCode("missionCode2");
    }

    public static DroneMission getDroneMissionRandomSampleGenerator() {
        return new DroneMission().id(longCount.incrementAndGet()).missionCode(UUID.randomUUID().toString());
    }
}
