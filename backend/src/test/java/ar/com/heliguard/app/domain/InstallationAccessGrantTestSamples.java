package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InstallationAccessGrantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static InstallationAccessGrant getInstallationAccessGrantSample1() {
        return new InstallationAccessGrant().id(1L).reason("reason1");
    }

    public static InstallationAccessGrant getInstallationAccessGrantSample2() {
        return new InstallationAccessGrant().id(2L).reason("reason2");
    }

    public static InstallationAccessGrant getInstallationAccessGrantRandomSampleGenerator() {
        return new InstallationAccessGrant().id(longCount.incrementAndGet()).reason(UUID.randomUUID().toString());
    }
}
