package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.EvidenceSourceAsserts.*;
import static ar.com.heliguard.app.domain.EvidenceSourceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EvidenceSourceMapperTest {

    private EvidenceSourceMapper evidenceSourceMapper;

    @BeforeEach
    void setUp() {
        evidenceSourceMapper = new EvidenceSourceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEvidenceSourceSample1();
        var actual = evidenceSourceMapper.toEntity(evidenceSourceMapper.toDto(expected));
        assertEvidenceSourceAllPropertiesEquals(expected, actual);
    }
}
