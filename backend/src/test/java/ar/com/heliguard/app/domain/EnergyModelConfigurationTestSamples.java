package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EnergyModelConfigurationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static EnergyModelConfiguration getEnergyModelConfigurationSample1() {
        return new EnergyModelConfiguration()
            .id(1L)
            .algorithmCode("algorithmCode1")
            .configVersion("configVersion1")
            .parameterSnapshotRef("parameterSnapshotRef1")
            .inputFingerprint("inputFingerprint1");
    }

    public static EnergyModelConfiguration getEnergyModelConfigurationSample2() {
        return new EnergyModelConfiguration()
            .id(2L)
            .algorithmCode("algorithmCode2")
            .configVersion("configVersion2")
            .parameterSnapshotRef("parameterSnapshotRef2")
            .inputFingerprint("inputFingerprint2");
    }

    public static EnergyModelConfiguration getEnergyModelConfigurationRandomSampleGenerator() {
        return new EnergyModelConfiguration()
            .id(longCount.incrementAndGet())
            .algorithmCode(UUID.randomUUID().toString())
            .configVersion(UUID.randomUUID().toString())
            .parameterSnapshotRef(UUID.randomUUID().toString())
            .inputFingerprint(UUID.randomUUID().toString());
    }
}
