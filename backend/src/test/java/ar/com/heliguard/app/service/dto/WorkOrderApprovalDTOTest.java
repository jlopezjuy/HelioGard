package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class WorkOrderApprovalDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WorkOrderApprovalDTO.class);
        WorkOrderApprovalDTO workOrderApprovalDTO1 = new WorkOrderApprovalDTO();
        workOrderApprovalDTO1.setId(1L);
        WorkOrderApprovalDTO workOrderApprovalDTO2 = new WorkOrderApprovalDTO();
        assertThat(workOrderApprovalDTO1).isNotEqualTo(workOrderApprovalDTO2);
        workOrderApprovalDTO2.setId(workOrderApprovalDTO1.getId());
        assertThat(workOrderApprovalDTO1).isEqualTo(workOrderApprovalDTO2);
        workOrderApprovalDTO2.setId(2L);
        assertThat(workOrderApprovalDTO1).isNotEqualTo(workOrderApprovalDTO2);
        workOrderApprovalDTO1.setId(null);
        assertThat(workOrderApprovalDTO1).isNotEqualTo(workOrderApprovalDTO2);
    }
}
