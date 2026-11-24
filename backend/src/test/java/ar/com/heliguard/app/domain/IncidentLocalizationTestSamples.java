package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class IncidentLocalizationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static IncidentLocalization getIncidentLocalizationSample1() {
        return new IncidentLocalization().id(1L);
    }

    public static IncidentLocalization getIncidentLocalizationSample2() {
        return new IncidentLocalization().id(2L);
    }

    public static IncidentLocalization getIncidentLocalizationRandomSampleGenerator() {
        return new IncidentLocalization().id(longCount.incrementAndGet());
    }
}
