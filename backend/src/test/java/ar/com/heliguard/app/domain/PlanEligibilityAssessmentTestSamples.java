package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class PlanEligibilityAssessmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static PlanEligibilityAssessment getPlanEligibilityAssessmentSample1() {
        return new PlanEligibilityAssessment().id(1L);
    }

    public static PlanEligibilityAssessment getPlanEligibilityAssessmentSample2() {
        return new PlanEligibilityAssessment().id(2L);
    }

    public static PlanEligibilityAssessment getPlanEligibilityAssessmentRandomSampleGenerator() {
        return new PlanEligibilityAssessment().id(longCount.incrementAndGet());
    }
}
