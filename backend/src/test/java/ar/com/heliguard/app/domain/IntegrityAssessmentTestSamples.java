package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class IntegrityAssessmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static IntegrityAssessment getIntegrityAssessmentSample1() {
        return new IntegrityAssessment().id(1L).ruleVersion("ruleVersion1");
    }

    public static IntegrityAssessment getIntegrityAssessmentSample2() {
        return new IntegrityAssessment().id(2L).ruleVersion("ruleVersion2");
    }

    public static IntegrityAssessment getIntegrityAssessmentRandomSampleGenerator() {
        return new IntegrityAssessment().id(longCount.incrementAndGet()).ruleVersion(UUID.randomUUID().toString());
    }
}
