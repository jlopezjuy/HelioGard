package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class GatewayTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Gateway getGatewaySample1() {
        return new Gateway().id(1L).hardwareId("hardwareId1").firmwareVersion("firmwareVersion1");
    }

    public static Gateway getGatewaySample2() {
        return new Gateway().id(2L).hardwareId("hardwareId2").firmwareVersion("firmwareVersion2");
    }

    public static Gateway getGatewayRandomSampleGenerator() {
        return new Gateway()
            .id(longCount.incrementAndGet())
            .hardwareId(UUID.randomUUID().toString())
            .firmwareVersion(UUID.randomUUID().toString());
    }
}
