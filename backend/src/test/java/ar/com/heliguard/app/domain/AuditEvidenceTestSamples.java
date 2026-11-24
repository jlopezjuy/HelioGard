package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditEvidenceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AuditEvidence getAuditEvidenceSample1() {
        return new AuditEvidence().id(1L).storageKey("storageKey1").mimeType("mimeType1").sha256("sha2561");
    }

    public static AuditEvidence getAuditEvidenceSample2() {
        return new AuditEvidence().id(2L).storageKey("storageKey2").mimeType("mimeType2").sha256("sha2562");
    }

    public static AuditEvidence getAuditEvidenceRandomSampleGenerator() {
        return new AuditEvidence()
            .id(longCount.incrementAndGet())
            .storageKey(UUID.randomUUID().toString())
            .mimeType(UUID.randomUUID().toString())
            .sha256(UUID.randomUUID().toString());
    }
}
