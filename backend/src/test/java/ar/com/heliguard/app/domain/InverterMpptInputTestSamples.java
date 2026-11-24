package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InverterMpptInputTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static InverterMpptInput getInverterMpptInputSample1() {
        return new InverterMpptInput().id(1L).inputCode("inputCode1");
    }

    public static InverterMpptInput getInverterMpptInputSample2() {
        return new InverterMpptInput().id(2L).inputCode("inputCode2");
    }

    public static InverterMpptInput getInverterMpptInputRandomSampleGenerator() {
        return new InverterMpptInput().id(longCount.incrementAndGet()).inputCode(UUID.randomUUID().toString());
    }
}
