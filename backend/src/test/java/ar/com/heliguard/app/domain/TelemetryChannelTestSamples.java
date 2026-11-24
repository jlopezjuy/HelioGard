package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TelemetryChannelTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static TelemetryChannel getTelemetryChannelSample1() {
        return new TelemetryChannel().id(1L).externalChannelCode("externalChannelCode1").samplePeriodSeconds(1);
    }

    public static TelemetryChannel getTelemetryChannelSample2() {
        return new TelemetryChannel().id(2L).externalChannelCode("externalChannelCode2").samplePeriodSeconds(2);
    }

    public static TelemetryChannel getTelemetryChannelRandomSampleGenerator() {
        return new TelemetryChannel()
            .id(longCount.incrementAndGet())
            .externalChannelCode(UUID.randomUUID().toString())
            .samplePeriodSeconds(intCount.incrementAndGet());
    }
}
