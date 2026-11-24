package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.EnergyModelRunAsserts.*;
import static ar.com.heliguard.app.domain.EnergyModelRunTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnergyModelRunMapperTest {

    private EnergyModelRunMapper energyModelRunMapper;

    @BeforeEach
    void setUp() {
        energyModelRunMapper = new EnergyModelRunMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEnergyModelRunSample1();
        var actual = energyModelRunMapper.toEntity(energyModelRunMapper.toDto(expected));
        assertEnergyModelRunAllPropertiesEquals(expected, actual);
    }
}
