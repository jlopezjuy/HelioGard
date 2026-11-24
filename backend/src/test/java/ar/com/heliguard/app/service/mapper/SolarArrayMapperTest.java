package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.SolarArrayAsserts.*;
import static ar.com.heliguard.app.domain.SolarArrayTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolarArrayMapperTest {

    private SolarArrayMapper solarArrayMapper;

    @BeforeEach
    void setUp() {
        solarArrayMapper = new SolarArrayMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSolarArraySample1();
        var actual = solarArrayMapper.toEntity(solarArrayMapper.toDto(expected));
        assertSolarArrayAllPropertiesEquals(expected, actual);
    }
}
