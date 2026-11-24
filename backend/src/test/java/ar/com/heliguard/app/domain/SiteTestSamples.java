package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SiteTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Site getSiteSample1() {
        return new Site().id(1L).name("name1").address("address1").timeZone("timeZone1");
    }

    public static Site getSiteSample2() {
        return new Site().id(2L).name("name2").address("address2").timeZone("timeZone2");
    }

    public static Site getSiteRandomSampleGenerator() {
        return new Site()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString())
            .timeZone(UUID.randomUUID().toString());
    }
}
