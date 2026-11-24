package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.WorkOrderAsserts.*;
import static ar.com.heliguard.app.domain.WorkOrderTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkOrderMapperTest {

    private WorkOrderMapper workOrderMapper;

    @BeforeEach
    void setUp() {
        workOrderMapper = new WorkOrderMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWorkOrderSample1();
        var actual = workOrderMapper.toEntity(workOrderMapper.toDto(expected));
        assertWorkOrderAllPropertiesEquals(expected, actual);
    }
}
