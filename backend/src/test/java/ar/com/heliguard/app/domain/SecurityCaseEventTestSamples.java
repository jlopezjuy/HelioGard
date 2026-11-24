package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SecurityCaseEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static SecurityCaseEvent getSecurityCaseEventSample1() {
        return new SecurityCaseEvent().id(1L).eventCode("eventCode1");
    }

    public static SecurityCaseEvent getSecurityCaseEventSample2() {
        return new SecurityCaseEvent().id(2L).eventCode("eventCode2");
    }

    public static SecurityCaseEvent getSecurityCaseEventRandomSampleGenerator() {
        return new SecurityCaseEvent().id(longCount.incrementAndGet()).eventCode(UUID.randomUUID().toString());
    }
}
