package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.MaintenancePlanAsserts.*;
import static ar.com.heliguard.app.domain.MaintenancePlanTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MaintenancePlanMapperTest {

    private MaintenancePlanMapper maintenancePlanMapper;

    @BeforeEach
    void setUp() {
        maintenancePlanMapper = new MaintenancePlanMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMaintenancePlanSample1();
        var actual = maintenancePlanMapper.toEntity(maintenancePlanMapper.toDto(expected));
        assertMaintenancePlanAllPropertiesEquals(expected, actual);
    }
}
