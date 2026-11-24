package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.SolarStringAsserts.*;
import static ar.com.heliguard.app.domain.SolarStringTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolarStringMapperTest {

    private SolarStringMapper solarStringMapper;

    @BeforeEach
    void setUp() {
        solarStringMapper = new SolarStringMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSolarStringSample1();
        var actual = solarStringMapper.toEntity(solarStringMapper.toDto(expected));
        assertSolarStringAllPropertiesEquals(expected, actual);
    }
}
