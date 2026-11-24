package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MetricDefinitionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static MetricDefinition getMetricDefinitionSample1() {
        return new MetricDefinition().id(1L).code("code1").unitCode("unitCode1").description("description1");
    }

    public static MetricDefinition getMetricDefinitionSample2() {
        return new MetricDefinition().id(2L).code("code2").unitCode("unitCode2").description("description2");
    }

    public static MetricDefinition getMetricDefinitionRandomSampleGenerator() {
        return new MetricDefinition()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .unitCode(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
