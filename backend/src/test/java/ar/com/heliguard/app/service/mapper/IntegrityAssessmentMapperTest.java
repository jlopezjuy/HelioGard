package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.IntegrityAssessmentAsserts.*;
import static ar.com.heliguard.app.domain.IntegrityAssessmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IntegrityAssessmentMapperTest {

    private IntegrityAssessmentMapper integrityAssessmentMapper;

    @BeforeEach
    void setUp() {
        integrityAssessmentMapper = new IntegrityAssessmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getIntegrityAssessmentSample1();
        var actual = integrityAssessmentMapper.toEntity(integrityAssessmentMapper.toDto(expected));
        assertIntegrityAssessmentAllPropertiesEquals(expected, actual);
    }
}
