package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InstallationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Installation getInstallationSample1() {
        return new Installation().id(1L).code("code1").name("name1");
    }

    public static Installation getInstallationSample2() {
        return new Installation().id(2L).code("code2").name("name2");
    }

    public static Installation getInstallationRandomSampleGenerator() {
        return new Installation().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).name(UUID.randomUUID().toString());
    }
}
