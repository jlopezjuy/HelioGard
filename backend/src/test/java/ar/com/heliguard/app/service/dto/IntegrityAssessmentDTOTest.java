package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntegrityAssessmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntegrityAssessmentDTO.class);
        IntegrityAssessmentDTO integrityAssessmentDTO1 = new IntegrityAssessmentDTO();
        integrityAssessmentDTO1.setId(1L);
        IntegrityAssessmentDTO integrityAssessmentDTO2 = new IntegrityAssessmentDTO();
        assertThat(integrityAssessmentDTO1).isNotEqualTo(integrityAssessmentDTO2);
        integrityAssessmentDTO2.setId(integrityAssessmentDTO1.getId());
        assertThat(integrityAssessmentDTO1).isEqualTo(integrityAssessmentDTO2);
        integrityAssessmentDTO2.setId(2L);
        assertThat(integrityAssessmentDTO1).isNotEqualTo(integrityAssessmentDTO2);
        integrityAssessmentDTO1.setId(null);
        assertThat(integrityAssessmentDTO1).isNotEqualTo(integrityAssessmentDTO2);
    }
}
