package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.LossAssessmentAsserts.*;
import static ar.com.heliguard.app.domain.LossAssessmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LossAssessmentMapperTest {

    private LossAssessmentMapper lossAssessmentMapper;

    @BeforeEach
    void setUp() {
        lossAssessmentMapper = new LossAssessmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLossAssessmentSample1();
        var actual = lossAssessmentMapper.toEntity(lossAssessmentMapper.toDto(expected));
        assertLossAssessmentAllPropertiesEquals(expected, actual);
    }
}
