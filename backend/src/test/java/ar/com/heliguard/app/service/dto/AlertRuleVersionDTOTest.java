package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AlertRuleVersionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AlertRuleVersionDTO.class);
        AlertRuleVersionDTO alertRuleVersionDTO1 = new AlertRuleVersionDTO();
        alertRuleVersionDTO1.setId(1L);
        AlertRuleVersionDTO alertRuleVersionDTO2 = new AlertRuleVersionDTO();
        assertThat(alertRuleVersionDTO1).isNotEqualTo(alertRuleVersionDTO2);
        alertRuleVersionDTO2.setId(alertRuleVersionDTO1.getId());
        assertThat(alertRuleVersionDTO1).isEqualTo(alertRuleVersionDTO2);
        alertRuleVersionDTO2.setId(2L);
        assertThat(alertRuleVersionDTO1).isNotEqualTo(alertRuleVersionDTO2);
        alertRuleVersionDTO1.setId(null);
        assertThat(alertRuleVersionDTO1).isNotEqualTo(alertRuleVersionDTO2);
    }
}
