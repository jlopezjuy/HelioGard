package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AuditAnswerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static AuditAnswer getAuditAnswerSample1() {
        return new AuditAnswer().id(1L).answerUnitCode("answerUnitCode1");
    }

    public static AuditAnswer getAuditAnswerSample2() {
        return new AuditAnswer().id(2L).answerUnitCode("answerUnitCode2");
    }

    public static AuditAnswer getAuditAnswerRandomSampleGenerator() {
        return new AuditAnswer().id(longCount.incrementAndGet()).answerUnitCode(UUID.randomUUID().toString());
    }
}
