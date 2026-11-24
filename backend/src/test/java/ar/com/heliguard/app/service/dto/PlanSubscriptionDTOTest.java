package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PlanSubscriptionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlanSubscriptionDTO.class);
        PlanSubscriptionDTO planSubscriptionDTO1 = new PlanSubscriptionDTO();
        planSubscriptionDTO1.setId(1L);
        PlanSubscriptionDTO planSubscriptionDTO2 = new PlanSubscriptionDTO();
        assertThat(planSubscriptionDTO1).isNotEqualTo(planSubscriptionDTO2);
        planSubscriptionDTO2.setId(planSubscriptionDTO1.getId());
        assertThat(planSubscriptionDTO1).isEqualTo(planSubscriptionDTO2);
        planSubscriptionDTO2.setId(2L);
        assertThat(planSubscriptionDTO1).isNotEqualTo(planSubscriptionDTO2);
        planSubscriptionDTO1.setId(null);
        assertThat(planSubscriptionDTO1).isNotEqualTo(planSubscriptionDTO2);
    }
}
