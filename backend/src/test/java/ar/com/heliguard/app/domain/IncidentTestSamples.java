package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class IncidentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Incident getIncidentSample1() {
        return new Incident().id(1L).title("title1").dedupKey("dedupKey1");
    }

    public static Incident getIncidentSample2() {
        return new Incident().id(2L).title("title2").dedupKey("dedupKey2");
    }

    public static Incident getIncidentRandomSampleGenerator() {
        return new Incident().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).dedupKey(UUID.randomUUID().toString());
    }
}
