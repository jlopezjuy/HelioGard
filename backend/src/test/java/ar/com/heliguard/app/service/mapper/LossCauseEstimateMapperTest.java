package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.LossCauseEstimateAsserts.*;
import static ar.com.heliguard.app.domain.LossCauseEstimateTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LossCauseEstimateMapperTest {

    private LossCauseEstimateMapper lossCauseEstimateMapper;

    @BeforeEach
    void setUp() {
        lossCauseEstimateMapper = new LossCauseEstimateMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLossCauseEstimateSample1();
        var actual = lossCauseEstimateMapper.toEntity(lossCauseEstimateMapper.toDto(expected));
        assertLossCauseEstimateAllPropertiesEquals(expected, actual);
    }
}
