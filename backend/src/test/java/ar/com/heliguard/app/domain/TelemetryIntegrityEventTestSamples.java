package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TelemetryIntegrityEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static TelemetryIntegrityEvent getTelemetryIntegrityEventSample1() {
        return new TelemetryIntegrityEvent()
            .id(1L)
            .eventUid("eventUid1")
            .bootId("bootId1")
            .sequenceNumber(1L)
            .payloadSha256("payloadSha2561");
    }

    public static TelemetryIntegrityEvent getTelemetryIntegrityEventSample2() {
        return new TelemetryIntegrityEvent()
            .id(2L)
            .eventUid("eventUid2")
            .bootId("bootId2")
            .sequenceNumber(2L)
            .payloadSha256("payloadSha2562");
    }

    public static TelemetryIntegrityEvent getTelemetryIntegrityEventRandomSampleGenerator() {
        return new TelemetryIntegrityEvent()
            .id(longCount.incrementAndGet())
            .eventUid(UUID.randomUUID().toString())
            .bootId(UUID.randomUUID().toString())
            .sequenceNumber(longCount.incrementAndGet())
            .payloadSha256(UUID.randomUUID().toString());
    }
}
