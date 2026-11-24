package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.WorkOrderTaskAsserts.*;
import static ar.com.heliguard.app.domain.WorkOrderTaskTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkOrderTaskMapperTest {

    private WorkOrderTaskMapper workOrderTaskMapper;

    @BeforeEach
    void setUp() {
        workOrderTaskMapper = new WorkOrderTaskMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWorkOrderTaskSample1();
        var actual = workOrderTaskMapper.toEntity(workOrderTaskMapper.toDto(expected));
        assertWorkOrderTaskAllPropertiesEquals(expected, actual);
    }
}
