package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SolarSectorTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static SolarSector getSolarSectorSample1() {
        return new SolarSector().id(1L).sectorCode("sectorCode1").name("name1").geometryReference("geometryReference1");
    }

    public static SolarSector getSolarSectorSample2() {
        return new SolarSector().id(2L).sectorCode("sectorCode2").name("name2").geometryReference("geometryReference2");
    }

    public static SolarSector getSolarSectorRandomSampleGenerator() {
        return new SolarSector()
            .id(longCount.incrementAndGet())
            .sectorCode(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .geometryReference(UUID.randomUUID().toString());
    }
}
