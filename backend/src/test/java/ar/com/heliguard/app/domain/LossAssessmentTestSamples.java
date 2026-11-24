package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LossAssessmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static LossAssessment getLossAssessmentSample1() {
        return new LossAssessment().id(1L).methodology("methodology1");
    }

    public static LossAssessment getLossAssessmentSample2() {
        return new LossAssessment().id(2L).methodology("methodology2");
    }

    public static LossAssessment getLossAssessmentRandomSampleGenerator() {
        return new LossAssessment().id(longCount.incrementAndGet()).methodology(UUID.randomUUID().toString());
    }
}
