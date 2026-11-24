package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RecoveryAssessmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static RecoveryAssessment getRecoveryAssessmentSample1() {
        return new RecoveryAssessment().id(1L).baselineMethod("baselineMethod1");
    }

    public static RecoveryAssessment getRecoveryAssessmentSample2() {
        return new RecoveryAssessment().id(2L).baselineMethod("baselineMethod2");
    }

    public static RecoveryAssessment getRecoveryAssessmentRandomSampleGenerator() {
        return new RecoveryAssessment().id(longCount.incrementAndGet()).baselineMethod(UUID.randomUUID().toString());
    }
}
