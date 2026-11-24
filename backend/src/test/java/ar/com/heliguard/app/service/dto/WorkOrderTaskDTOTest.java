package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkOrderTaskDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkOrderTaskDTO.class);
        WorkOrderTaskDTO workOrderTaskDTO1 = new WorkOrderTaskDTO();
        workOrderTaskDTO1.setId(1L);
        WorkOrderTaskDTO workOrderTaskDTO2 = new WorkOrderTaskDTO();
        assertThat(workOrderTaskDTO1).isNotEqualTo(workOrderTaskDTO2);
        workOrderTaskDTO2.setId(workOrderTaskDTO1.getId());
        assertThat(workOrderTaskDTO1).isEqualTo(workOrderTaskDTO2);
        workOrderTaskDTO2.setId(2L);
        assertThat(workOrderTaskDTO1).isNotEqualTo(workOrderTaskDTO2);
        workOrderTaskDTO1.setId(null);
        assertThat(workOrderTaskDTO1).isNotEqualTo(workOrderTaskDTO2);
    }
}
