package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.WorkOrderAssignmentAsserts.*;
import static ar.com.heliguard.app.domain.WorkOrderAssignmentTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkOrderAssignmentMapperTest {

    private WorkOrderAssignmentMapper workOrderAssignmentMapper;

    @BeforeEach
    void setUp() {
        workOrderAssignmentMapper = new WorkOrderAssignmentMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWorkOrderAssignmentSample1();
        var actual = workOrderAssignmentMapper.toEntity(workOrderAssignmentMapper.toDto(expected));
        assertWorkOrderAssignmentAllPropertiesEquals(expected, actual);
    }
}
