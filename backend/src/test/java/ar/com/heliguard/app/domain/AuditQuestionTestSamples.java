package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AuditQuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static AuditQuestion getAuditQuestionSample1() {
        return new AuditQuestion().id(1L).questionCode("questionCode1").label("label1").answerType("answerType1").sortOrder(1);
    }

    public static AuditQuestion getAuditQuestionSample2() {
        return new AuditQuestion().id(2L).questionCode("questionCode2").label("label2").answerType("answerType2").sortOrder(2);
    }

    public static AuditQuestion getAuditQuestionRandomSampleGenerator() {
        return new AuditQuestion()
            .id(longCount.incrementAndGet())
            .questionCode(UUID.randomUUID().toString())
            .label(UUID.randomUUID().toString())
            .answerType(UUID.randomUUID().toString())
            .sortOrder(intCount.incrementAndGet());
    }
}
