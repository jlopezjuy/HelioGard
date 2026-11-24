package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EquipmentConnectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static EquipmentConnection getEquipmentConnectionSample1() {
        return new EquipmentConnection().id(1L).connectionCode("connectionCode1");
    }

    public static EquipmentConnection getEquipmentConnectionSample2() {
        return new EquipmentConnection().id(2L).connectionCode("connectionCode2");
    }

    public static EquipmentConnection getEquipmentConnectionRandomSampleGenerator() {
        return new EquipmentConnection().id(longCount.incrementAndGet()).connectionCode(UUID.randomUUID().toString());
    }
}
