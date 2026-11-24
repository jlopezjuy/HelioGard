package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class VerificationResultTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static VerificationResult getVerificationResultSample1() {
        return new VerificationResult().id(1L).baselineMethod("baselineMethod1");
    }

    public static VerificationResult getVerificationResultSample2() {
        return new VerificationResult().id(2L).baselineMethod("baselineMethod2");
    }

    public static VerificationResult getVerificationResultRandomSampleGenerator() {
        return new VerificationResult().id(longCount.incrementAndGet()).baselineMethod(UUID.randomUUID().toString());
    }
}
