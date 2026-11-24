package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EconomicAssessmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static EconomicAssessment getEconomicAssessmentSample1() {
        return new EconomicAssessment().id(1L).currencyCode("currencyCode1").tariffSnapshotNote("tariffSnapshotNote1");
    }

    public static EconomicAssessment getEconomicAssessmentSample2() {
        return new EconomicAssessment().id(2L).currencyCode("currencyCode2").tariffSnapshotNote("tariffSnapshotNote2");
    }

    public static EconomicAssessment getEconomicAssessmentRandomSampleGenerator() {
        return new EconomicAssessment()
            .id(longCount.incrementAndGet())
            .currencyCode(UUID.randomUUID().toString())
            .tariffSnapshotNote(UUID.randomUUID().toString());
    }
}
