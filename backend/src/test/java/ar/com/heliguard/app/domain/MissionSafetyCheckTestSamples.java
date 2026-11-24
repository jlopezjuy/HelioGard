package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MissionSafetyCheckTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static MissionSafetyCheck getMissionSafetyCheckSample1() {
        return new MissionSafetyCheck().id(1L).checkCode("checkCode1").evidenceReference("evidenceReference1");
    }

    public static MissionSafetyCheck getMissionSafetyCheckSample2() {
        return new MissionSafetyCheck().id(2L).checkCode("checkCode2").evidenceReference("evidenceReference2");
    }

    public static MissionSafetyCheck getMissionSafetyCheckRandomSampleGenerator() {
        return new MissionSafetyCheck()
            .id(longCount.incrementAndGet())
            .checkCode(UUID.randomUUID().toString())
            .evidenceReference(UUID.randomUUID().toString());
    }
}
