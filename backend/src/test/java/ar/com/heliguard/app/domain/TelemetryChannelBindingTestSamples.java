package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TelemetryChannelBindingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TelemetryChannelBinding getTelemetryChannelBindingSample1() {
        return new TelemetryChannelBinding().id(1L).externalPath("externalPath1");
    }

    public static TelemetryChannelBinding getTelemetryChannelBindingSample2() {
        return new TelemetryChannelBinding().id(2L).externalPath("externalPath2");
    }

    public static TelemetryChannelBinding getTelemetryChannelBindingRandomSampleGenerator() {
        return new TelemetryChannelBinding().id(longCount.incrementAndGet()).externalPath(UUID.randomUUID().toString());
    }
}
