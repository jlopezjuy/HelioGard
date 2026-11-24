package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditFindingTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AuditFinding getAuditFindingSample1() {
        return new AuditFinding().id(1L).title("title1");
    }

    public static AuditFinding getAuditFindingSample2() {
        return new AuditFinding().id(2L).title("title2");
    }

    public static AuditFinding getAuditFindingRandomSampleGenerator() {
        return new AuditFinding().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString());
    }
}
