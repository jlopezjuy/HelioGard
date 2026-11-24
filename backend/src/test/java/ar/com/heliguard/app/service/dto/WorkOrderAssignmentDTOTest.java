package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkOrderAssignmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkOrderAssignmentDTO.class);
        WorkOrderAssignmentDTO workOrderAssignmentDTO1 = new WorkOrderAssignmentDTO();
        workOrderAssignmentDTO1.setId(1L);
        WorkOrderAssignmentDTO workOrderAssignmentDTO2 = new WorkOrderAssignmentDTO();
        assertThat(workOrderAssignmentDTO1).isNotEqualTo(workOrderAssignmentDTO2);
        workOrderAssignmentDTO2.setId(workOrderAssignmentDTO1.getId());
        assertThat(workOrderAssignmentDTO1).isEqualTo(workOrderAssignmentDTO2);
        workOrderAssignmentDTO2.setId(2L);
        assertThat(workOrderAssignmentDTO1).isNotEqualTo(workOrderAssignmentDTO2);
        workOrderAssignmentDTO1.setId(null);
        assertThat(workOrderAssignmentDTO1).isNotEqualTo(workOrderAssignmentDTO2);
    }
}
