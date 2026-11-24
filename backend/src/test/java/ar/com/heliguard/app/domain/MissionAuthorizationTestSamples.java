package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MissionAuthorizationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static MissionAuthorization getMissionAuthorizationSample1() {
        return new MissionAuthorization().id(1L).operatorReference("operatorReference1");
    }

    public static MissionAuthorization getMissionAuthorizationSample2() {
        return new MissionAuthorization().id(2L).operatorReference("operatorReference2");
    }

    public static MissionAuthorization getMissionAuthorizationRandomSampleGenerator() {
        return new MissionAuthorization().id(longCount.incrementAndGet()).operatorReference(UUID.randomUUID().toString());
    }
}
