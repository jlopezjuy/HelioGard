package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RecoveryAssessmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecoveryAssessmentDTO.class);
        RecoveryAssessmentDTO recoveryAssessmentDTO1 = new RecoveryAssessmentDTO();
        recoveryAssessmentDTO1.setId(1L);
        RecoveryAssessmentDTO recoveryAssessmentDTO2 = new RecoveryAssessmentDTO();
        assertThat(recoveryAssessmentDTO1).isNotEqualTo(recoveryAssessmentDTO2);
        recoveryAssessmentDTO2.setId(recoveryAssessmentDTO1.getId());
        assertThat(recoveryAssessmentDTO1).isEqualTo(recoveryAssessmentDTO2);
        recoveryAssessmentDTO2.setId(2L);
        assertThat(recoveryAssessmentDTO1).isNotEqualTo(recoveryAssessmentDTO2);
        recoveryAssessmentDTO1.setId(null);
        assertThat(recoveryAssessmentDTO1).isNotEqualTo(recoveryAssessmentDTO2);
    }
}
