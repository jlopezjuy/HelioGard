package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.EquipmentModelAsserts.*;
import static ar.com.heliguard.app.domain.EquipmentModelTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EquipmentModelMapperTest {

    private EquipmentModelMapper equipmentModelMapper;

    @BeforeEach
    void setUp() {
        equipmentModelMapper = new EquipmentModelMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEquipmentModelSample1();
        var actual = equipmentModelMapper.toEntity(equipmentModelMapper.toDto(expected));
        assertEquipmentModelAllPropertiesEquals(expected, actual);
    }
}
