package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class WeatherIngestionBatchTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static WeatherIngestionBatch getWeatherIngestionBatchSample1() {
        return new WeatherIngestionBatch()
            .id(1L)
            .providerBatchCode("providerBatchCode1")
            .datasetCode("datasetCode1")
            .qualityNote("qualityNote1");
    }

    public static WeatherIngestionBatch getWeatherIngestionBatchSample2() {
        return new WeatherIngestionBatch()
            .id(2L)
            .providerBatchCode("providerBatchCode2")
            .datasetCode("datasetCode2")
            .qualityNote("qualityNote2");
    }

    public static WeatherIngestionBatch getWeatherIngestionBatchRandomSampleGenerator() {
        return new WeatherIngestionBatch()
            .id(longCount.incrementAndGet())
            .providerBatchCode(UUID.randomUUID().toString())
            .datasetCode(UUID.randomUUID().toString())
            .qualityNote(UUID.randomUUID().toString());
    }
}
