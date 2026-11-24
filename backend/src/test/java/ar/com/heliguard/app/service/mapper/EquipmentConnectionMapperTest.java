package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.EquipmentConnectionAsserts.*;
import static ar.com.heliguard.app.domain.EquipmentConnectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EquipmentConnectionMapperTest {

    private EquipmentConnectionMapper equipmentConnectionMapper;

    @BeforeEach
    void setUp() {
        equipmentConnectionMapper = new EquipmentConnectionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEquipmentConnectionSample1();
        var actual = equipmentConnectionMapper.toEntity(equipmentConnectionMapper.toDto(expected));
        assertEquipmentConnectionAllPropertiesEquals(expected, actual);
    }
}
