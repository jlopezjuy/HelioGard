package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PlanBenefitTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static PlanBenefit getPlanBenefitSample1() {
        return new PlanBenefit()
            .id(1L)
            .benefitCode("benefitCode1")
            .description("description1")
            .unitCode("unitCode1")
            .intervalMonths(1)
            .termsReference("termsReference1");
    }

    public static PlanBenefit getPlanBenefitSample2() {
        return new PlanBenefit()
            .id(2L)
            .benefitCode("benefitCode2")
            .description("description2")
            .unitCode("unitCode2")
            .intervalMonths(2)
            .termsReference("termsReference2");
    }

    public static PlanBenefit getPlanBenefitRandomSampleGenerator() {
        return new PlanBenefit()
            .id(longCount.incrementAndGet())
            .benefitCode(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .unitCode(UUID.randomUUID().toString())
            .intervalMonths(intCount.incrementAndGet())
            .termsReference(UUID.randomUUID().toString());
    }
}
