package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanBenefitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanBenefitDTO.class);
        PlanBenefitDTO planBenefitDTO1 = new PlanBenefitDTO();
        planBenefitDTO1.setId(1L);
        PlanBenefitDTO planBenefitDTO2 = new PlanBenefitDTO();
        assertThat(planBenefitDTO1).isNotEqualTo(planBenefitDTO2);
        planBenefitDTO2.setId(planBenefitDTO1.getId());
        assertThat(planBenefitDTO1).isEqualTo(planBenefitDTO2);
        planBenefitDTO2.setId(2L);
        assertThat(planBenefitDTO1).isNotEqualTo(planBenefitDTO2);
        planBenefitDTO1.setId(null);
        assertThat(planBenefitDTO1).isNotEqualTo(planBenefitDTO2);
    }
}
