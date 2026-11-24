package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.SolarSectorAsserts.*;
import static ar.com.heliguard.app.domain.SolarSectorTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolarSectorMapperTest {

    private SolarSectorMapper solarSectorMapper;

    @BeforeEach
    void setUp() {
        solarSectorMapper = new SolarSectorMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getSolarSectorSample1();
        var actual = solarSectorMapper.toEntity(solarSectorMapper.toDto(expected));
        assertSolarSectorAllPropertiesEquals(expected, actual);
    }
}
