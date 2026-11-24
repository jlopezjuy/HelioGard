package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditReportSignatureTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AuditReportSignature getAuditReportSignatureSample1() {
        return new AuditReportSignature().id(1L).documentSha256("documentSha2561").signatureReference("signatureReference1");
    }

    public static AuditReportSignature getAuditReportSignatureSample2() {
        return new AuditReportSignature().id(2L).documentSha256("documentSha2562").signatureReference("signatureReference2");
    }

    public static AuditReportSignature getAuditReportSignatureRandomSampleGenerator() {
        return new AuditReportSignature()
            .id(longCount.incrementAndGet())
            .documentSha256(UUID.randomUUID().toString())
            .signatureReference(UUID.randomUUID().toString());
    }
}
