package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.MaintenancePlanVersionAsserts.*;
import static ar.com.heliguard.app.domain.MaintenancePlanVersionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MaintenancePlanVersionMapperTest {

    private MaintenancePlanVersionMapper maintenancePlanVersionMapper;

    @BeforeEach
    void setUp() {
        maintenancePlanVersionMapper = new MaintenancePlanVersionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMaintenancePlanVersionSample1();
        var actual = maintenancePlanVersionMapper.toEntity(maintenancePlanVersionMapper.toDto(expected));
        assertMaintenancePlanVersionAllPropertiesEquals(expected, actual);
    }
}
