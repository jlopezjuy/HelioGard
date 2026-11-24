package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SecurityCaseEvidenceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static SecurityCaseEvidence getSecurityCaseEvidenceSample1() {
        return new SecurityCaseEvidence()
            .id(1L)
            .evidenceCode("evidenceCode1")
            .storageReference("storageReference1")
            .sha256("sha2561")
            .note("note1");
    }

    public static SecurityCaseEvidence getSecurityCaseEvidenceSample2() {
        return new SecurityCaseEvidence()
            .id(2L)
            .evidenceCode("evidenceCode2")
            .storageReference("storageReference2")
            .sha256("sha2562")
            .note("note2");
    }

    public static SecurityCaseEvidence getSecurityCaseEvidenceRandomSampleGenerator() {
        return new SecurityCaseEvidence()
            .id(longCount.incrementAndGet())
            .evidenceCode(UUID.randomUUID().toString())
            .storageReference(UUID.randomUUID().toString())
            .sha256(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString());
    }
}
