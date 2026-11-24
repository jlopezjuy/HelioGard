package ar.com.heliguard.app.service.mapper;

import static ar.com.heliguard.app.domain.WorkOrderApprovalAsserts.*;
import static ar.com.heliguard.app.domain.WorkOrderApprovalTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WorkOrderApprovalMapperTest {

    private WorkOrderApprovalMapper workOrderApprovalMapper;

    @BeforeEach
    void setUp() {
        workOrderApprovalMapper = new WorkOrderApprovalMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWorkOrderApprovalSample1();
        var actual = workOrderApprovalMapper.toEntity(workOrderApprovalMapper.toDto(expected));
        assertWorkOrderApprovalAllPropertiesEquals(expected, actual);
    }
}
