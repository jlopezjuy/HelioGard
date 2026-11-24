package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DeviceIdentityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static DeviceIdentity getDeviceIdentitySample1() {
        return new DeviceIdentity().id(1L).deviceUid("deviceUid1").keyFingerprint("keyFingerprint1").protocolName("protocolName1");
    }

    public static DeviceIdentity getDeviceIdentitySample2() {
        return new DeviceIdentity().id(2L).deviceUid("deviceUid2").keyFingerprint("keyFingerprint2").protocolName("protocolName2");
    }

    public static DeviceIdentity getDeviceIdentityRandomSampleGenerator() {
        return new DeviceIdentity()
            .id(longCount.incrementAndGet())
            .deviceUid(UUID.randomUUID().toString())
            .keyFingerprint(UUID.randomUUID().toString())
            .protocolName(UUID.randomUUID().toString());
    }
}
