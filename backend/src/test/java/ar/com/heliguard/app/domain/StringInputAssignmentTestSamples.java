package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StringInputAssignmentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static StringInputAssignment getStringInputAssignmentSample1() {
        return new StringInputAssignment().id(1L).notes("notes1");
    }

    public static StringInputAssignment getStringInputAssignmentSample2() {
        return new StringInputAssignment().id(2L).notes("notes2");
    }

    public static StringInputAssignment getStringInputAssignmentRandomSampleGenerator() {
        return new StringInputAssignment().id(longCount.incrementAndGet()).notes(UUID.randomUUID().toString());
    }
}
