package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanEligibilityAssessmentDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanEligibilityAssessmentDTO.class);
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO1 = new PlanEligibilityAssessmentDTO();
        planEligibilityAssessmentDTO1.setId(1L);
        PlanEligibilityAssessmentDTO planEligibilityAssessmentDTO2 = new PlanEligibilityAssessmentDTO();
        assertThat(planEligibilityAssessmentDTO1).isNotEqualTo(planEligibilityAssessmentDTO2);
        planEligibilityAssessmentDTO2.setId(planEligibilityAssessmentDTO1.getId());
        assertThat(planEligibilityAssessmentDTO1).isEqualTo(planEligibilityAssessmentDTO2);
        planEligibilityAssessmentDTO2.setId(2L);
        assertThat(planEligibilityAssessmentDTO1).isNotEqualTo(planEligibilityAssessmentDTO2);
        planEligibilityAssessmentDTO1.setId(null);
        assertThat(planEligibilityAssessmentDTO1).isNotEqualTo(planEligibilityAssessmentDTO2);
    }
}
