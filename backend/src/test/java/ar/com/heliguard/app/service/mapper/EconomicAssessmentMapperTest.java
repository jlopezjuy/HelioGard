package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.EconomicAssessmentAsserts.*;
import static ar.com.heliguard.app.domain.EconomicAssessmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EconomicAssessmentMapperTest {

    private EconomicAssessmentMapper economicAssessmentMapper;

    @BeforeEach
    void setUp() {
        economicAssessmentMapper = new EconomicAssessmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEconomicAssessmentSample1();
        var actual = economicAssessmentMapper.toEntity(economicAssessmentMapper.toDto(expected));
        assertEconomicAssessmentAllPropertiesEquals(expected, actual);
    }
}
