package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EnergyModelRunTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static EnergyModelRun getEnergyModelRunSample1() {
        return new EnergyModelRun()
            .id(1L)
            .modelName("modelName1")
            .modelVersion("modelVersion1")
            .inputFingerprint("inputFingerprint1")
            .qualityNote("qualityNote1");
    }

    public static EnergyModelRun getEnergyModelRunSample2() {
        return new EnergyModelRun()
            .id(2L)
            .modelName("modelName2")
            .modelVersion("modelVersion2")
            .inputFingerprint("inputFingerprint2")
            .qualityNote("qualityNote2");
    }

    public static EnergyModelRun getEnergyModelRunRandomSampleGenerator() {
        return new EnergyModelRun()
            .id(longCount.incrementAndGet())
            .modelName(UUID.randomUUID().toString())
            .modelVersion(UUID.randomUUID().toString())
            .inputFingerprint(UUID.randomUUID().toString())
            .qualityNote(UUID.randomUUID().toString());
    }
}
