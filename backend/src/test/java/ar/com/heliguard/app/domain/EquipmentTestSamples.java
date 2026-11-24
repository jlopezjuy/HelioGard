package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EquipmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static Equipment getEquipmentSample1() {
        return new Equipment().id(1L).tag("tag1").serialNumber("serialNumber1");
    }

    public static Equipment getEquipmentSample2() {
        return new Equipment().id(2L).tag("tag2").serialNumber("serialNumber2");
    }

    public static Equipment getEquipmentRandomSampleGenerator() {
        return new Equipment().id(longCount.incrementAndGet()).tag(UUID.randomUUID().toString()).serialNumber(UUID.randomUUID().toString());
    }
}
