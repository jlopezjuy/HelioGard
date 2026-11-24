package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ClimateRiskProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ClimateRiskProfile getClimateRiskProfileSample1() {
        return new ClimateRiskProfile().id(1L).code("code1").profileVersion("profileVersion1").description("description1");
    }

    public static ClimateRiskProfile getClimateRiskProfileSample2() {
        return new ClimateRiskProfile().id(2L).code("code2").profileVersion("profileVersion2").description("description2");
    }

    public static ClimateRiskProfile getClimateRiskProfileRandomSampleGenerator() {
        return new ClimateRiskProfile()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .profileVersion(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
