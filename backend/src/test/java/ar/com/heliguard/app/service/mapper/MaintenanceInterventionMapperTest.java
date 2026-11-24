package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.MaintenanceInterventionAsserts.*;
import static ar.com.heliguard.app.domain.MaintenanceInterventionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MaintenanceInterventionMapperTest {

    private MaintenanceInterventionMapper maintenanceInterventionMapper;

    @BeforeEach
    void setUp() {
        maintenanceInterventionMapper = new MaintenanceInterventionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMaintenanceInterventionSample1();
        var actual = maintenanceInterventionMapper.toEntity(maintenanceInterventionMapper.toDto(expected));
        assertMaintenanceInterventionAllPropertiesEquals(expected, actual);
    }
}
