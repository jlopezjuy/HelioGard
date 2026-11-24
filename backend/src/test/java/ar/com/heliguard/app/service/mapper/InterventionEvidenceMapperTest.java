package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.InterventionEvidenceAsserts.*;
import static ar.com.heliguard.app.domain.InterventionEvidenceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InterventionEvidenceMapperTest {

    private InterventionEvidenceMapper interventionEvidenceMapper;

    @BeforeEach
    void setUp() {
        interventionEvidenceMapper = new InterventionEvidenceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInterventionEvidenceSample1();
        var actual = interventionEvidenceMapper.toEntity(interventionEvidenceMapper.toDto(expected));
        assertInterventionEvidenceAllPropertiesEquals(expected, actual);
    }
}
