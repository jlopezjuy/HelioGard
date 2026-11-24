package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class IncidentEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static IncidentEvent getIncidentEventSample1() {
        return new IncidentEvent().id(1L).eventCode("eventCode1");
    }

    public static IncidentEvent getIncidentEventSample2() {
        return new IncidentEvent().id(2L).eventCode("eventCode2");
    }

    public static IncidentEvent getIncidentEventRandomSampleGenerator() {
        return new IncidentEvent().id(longCount.incrementAndGet()).eventCode(UUID.randomUUID().toString());
    }
}
