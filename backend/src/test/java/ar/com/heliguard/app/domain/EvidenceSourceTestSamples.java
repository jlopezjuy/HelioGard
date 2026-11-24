package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EvidenceSourceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static EvidenceSource getEvidenceSourceSample1() {
        return new EvidenceSource().id(1L).code("code1").providerName("providerName1").licenseReference("licenseReference1");
    }

    public static EvidenceSource getEvidenceSourceSample2() {
        return new EvidenceSource().id(2L).code("code2").providerName("providerName2").licenseReference("licenseReference2");
    }

    public static EvidenceSource getEvidenceSourceRandomSampleGenerator() {
        return new EvidenceSource()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .providerName(UUID.randomUUID().toString())
            .licenseReference(UUID.randomUUID().toString());
    }
}
