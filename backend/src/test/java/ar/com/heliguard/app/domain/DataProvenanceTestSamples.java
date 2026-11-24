package ar.com.heliguard.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DataProvenanceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    public static DataProvenance getDataProvenanceSample1() {
        return new DataProvenance()
            .id(1L)
            .sourceRecordKey("sourceRecordKey1")
            .sourceProductCode("sourceProductCode1")
            .contentSha256("contentSha2561")
            .qualityFlags("qualityFlags1")
            .algorithmVersion("algorithmVersion1");
    }

    public static DataProvenance getDataProvenanceSample2() {
        return new DataProvenance()
            .id(2L)
            .sourceRecordKey("sourceRecordKey2")
            .sourceProductCode("sourceProductCode2")
            .contentSha256("contentSha2562")
            .qualityFlags("qualityFlags2")
            .algorithmVersion("algorithmVersion2");
    }

    public static DataProvenance getDataProvenanceRandomSampleGenerator() {
        return new DataProvenance()
            .id(longCount.incrementAndGet())
            .sourceRecordKey(UUID.randomUUID().toString())
            .sourceProductCode(UUID.randomUUID().toString())
            .contentSha256(UUID.randomUUID().toString())
            .qualityFlags(UUID.randomUUID().toString())
            .algorithmVersion(UUID.randomUUID().toString());
    }
}
