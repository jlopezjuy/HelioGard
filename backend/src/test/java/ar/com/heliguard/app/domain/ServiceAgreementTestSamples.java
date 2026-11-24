package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceAgreementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ServiceAgreement getServiceAgreementSample1() {
        return new ServiceAgreement().id(1L);
    }

    public static ServiceAgreement getServiceAgreementSample2() {
        return new ServiceAgreement().id(2L);
    }

    public static ServiceAgreement getServiceAgreementRandomSampleGenerator() {
        return new ServiceAgreement().id(longCount.incrementAndGet());
    }
}
