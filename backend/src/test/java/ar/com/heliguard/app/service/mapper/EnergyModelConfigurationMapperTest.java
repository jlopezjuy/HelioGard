package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.EnergyModelConfigurationAsserts.*;
import static ar.com.heliguard.app.domain.EnergyModelConfigurationTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EnergyModelConfigurationMapperTest {

    private EnergyModelConfigurationMapper energyModelConfigurationMapper;

    @BeforeEach
    void setUp() {
        energyModelConfigurationMapper = new EnergyModelConfigurationMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEnergyModelConfigurationSample1();
        var actual = energyModelConfigurationMapper.toEntity(energyModelConfigurationMapper.toDto(expected));
        assertEnergyModelConfigurationAllPropertiesEquals(expected, actual);
    }
}
