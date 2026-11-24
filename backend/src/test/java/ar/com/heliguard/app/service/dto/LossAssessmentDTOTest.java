package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LossAssessmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LossAssessmentDTO.class);
        LossAssessmentDTO lossAssessmentDTO1 = new LossAssessmentDTO();
        lossAssessmentDTO1.setId(1L);
        LossAssessmentDTO lossAssessmentDTO2 = new LossAssessmentDTO();
        assertThat(lossAssessmentDTO1).isNotEqualTo(lossAssessmentDTO2);
        lossAssessmentDTO2.setId(lossAssessmentDTO1.getId());
        assertThat(lossAssessmentDTO1).isEqualTo(lossAssessmentDTO2);
        lossAssessmentDTO2.setId(2L);
        assertThat(lossAssessmentDTO1).isNotEqualTo(lossAssessmentDTO2);
        lossAssessmentDTO1.setId(null);
        assertThat(lossAssessmentDTO1).isNotEqualTo(lossAssessmentDTO2);
    }
}
