package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditTemplateTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AuditTemplate getAuditTemplateSample1() {
        return new AuditTemplate().id(1L).title("title1").templateVersion("templateVersion1");
    }

    public static AuditTemplate getAuditTemplateSample2() {
        return new AuditTemplate().id(2L).title("title2").templateVersion("templateVersion2");
    }

    public static AuditTemplate getAuditTemplateRandomSampleGenerator() {
        return new AuditTemplate()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .templateVersion(UUID.randomUUID().toString());
    }
}
