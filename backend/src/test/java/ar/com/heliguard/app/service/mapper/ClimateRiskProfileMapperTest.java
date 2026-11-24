package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.ClimateRiskProfileAsserts.*;
import static ar.com.heliguard.app.domain.ClimateRiskProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClimateRiskProfileMapperTest {

    private ClimateRiskProfileMapper climateRiskProfileMapper;

    @BeforeEach
    void setUp() {
        climateRiskProfileMapper = new ClimateRiskProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getClimateRiskProfileSample1();
        var actual = climateRiskProfileMapper.toEntity(climateRiskProfileMapper.toDto(expected));
        assertClimateRiskProfileAllPropertiesEquals(expected, actual);
    }
}
