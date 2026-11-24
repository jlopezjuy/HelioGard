package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EquipmentModelTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static EquipmentModel getEquipmentModelSample1() {
        return new EquipmentModel().id(1L).manufacturer("manufacturer1").modelCode("modelCode1").revision("revision1");
    }

    public static EquipmentModel getEquipmentModelSample2() {
        return new EquipmentModel().id(2L).manufacturer("manufacturer2").modelCode("modelCode2").revision("revision2");
    }

    public static EquipmentModel getEquipmentModelRandomSampleGenerator() {
        return new EquipmentModel()
            .id(longCount.incrementAndGet())
            .manufacturer(UUID.randomUUID().toString())
            .modelCode(UUID.randomUUID().toString())
            .revision(UUID.randomUUID().toString());
    }
}
