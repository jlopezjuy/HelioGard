package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.DataProvenanceAsserts.*;
import static ar.com.heliguard.app.domain.DataProvenanceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataProvenanceMapperTest {

    private DataProvenanceMapper dataProvenanceMapper;

    @BeforeEach
    void setUp() {
        dataProvenanceMapper = new DataProvenanceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getDataProvenanceSample1();
        var actual = dataProvenanceMapper.toEntity(dataProvenanceMapper.toDto(expected));
        assertDataProvenanceAllPropertiesEquals(expected, actual);
    }
}
