package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EconomicAssessmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EconomicAssessmentDTO.class);
        EconomicAssessmentDTO economicAssessmentDTO1 = new EconomicAssessmentDTO();
        economicAssessmentDTO1.setId(1L);
        EconomicAssessmentDTO economicAssessmentDTO2 = new EconomicAssessmentDTO();
        assertThat(economicAssessmentDTO1).isNotEqualTo(economicAssessmentDTO2);
        economicAssessmentDTO2.setId(economicAssessmentDTO1.getId());
        assertThat(economicAssessmentDTO1).isEqualTo(economicAssessmentDTO2);
        economicAssessmentDTO2.setId(2L);
        assertThat(economicAssessmentDTO1).isNotEqualTo(economicAssessmentDTO2);
        economicAssessmentDTO1.setId(null);
        assertThat(economicAssessmentDTO1).isNotEqualTo(economicAssessmentDTO2);
    }
}
