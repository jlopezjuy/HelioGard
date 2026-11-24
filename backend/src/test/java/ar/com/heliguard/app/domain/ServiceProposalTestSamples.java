package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ServiceProposalTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static ServiceProposal getServiceProposalSample1() {
        return new ServiceProposal().id(1L).referenceCode("referenceCode1").currencyCode("currencyCode1");
    }

    public static ServiceProposal getServiceProposalSample2() {
        return new ServiceProposal().id(2L).referenceCode("referenceCode2").currencyCode("currencyCode2");
    }

    public static ServiceProposal getServiceProposalRandomSampleGenerator() {
        return new ServiceProposal()
            .id(longCount.incrementAndGet())
            .referenceCode(UUID.randomUUID().toString())
            .currencyCode(UUID.randomUUID().toString());
    }
}
