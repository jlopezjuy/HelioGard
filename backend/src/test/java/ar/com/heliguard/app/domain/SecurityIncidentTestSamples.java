package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SecurityIncidentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static SecurityIncident getSecurityIncidentSample1() {
        return new SecurityIncident().id(1L).referenceCode("referenceCode1").title("title1");
    }

    public static SecurityIncident getSecurityIncidentSample2() {
        return new SecurityIncident().id(2L).referenceCode("referenceCode2").title("title2");
    }

    public static SecurityIncident getSecurityIncidentRandomSampleGenerator() {
        return new SecurityIncident()
            .id(longCount.incrementAndGet())
            .referenceCode(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString());
    }
}
