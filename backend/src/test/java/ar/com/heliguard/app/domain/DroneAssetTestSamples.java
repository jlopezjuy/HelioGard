package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DroneAssetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static DroneAsset getDroneAssetSample1() {
        return new DroneAsset().id(1L).assetCode("assetCode1").modelName("modelName1");
    }

    public static DroneAsset getDroneAssetSample2() {
        return new DroneAsset().id(2L).assetCode("assetCode2").modelName("modelName2");
    }

    public static DroneAsset getDroneAssetRandomSampleGenerator() {
        return new DroneAsset()
            .id(longCount.incrementAndGet())
            .assetCode(UUID.randomUUID().toString())
            .modelName(UUID.randomUUID().toString());
    }
}
