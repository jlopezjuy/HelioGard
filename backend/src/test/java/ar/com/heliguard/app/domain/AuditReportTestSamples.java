package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class AuditReportTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AuditReport getAuditReportSample1() {
        return new AuditReport().id(1L);
    }

    public static AuditReport getAuditReportSample2() {
        return new AuditReport().id(2L);
    }

    public static AuditReport getAuditReportRandomSampleGenerator() {
        return new AuditReport().id(longCount.incrementAndGet());
    }
}
