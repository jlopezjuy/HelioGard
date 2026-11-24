package ar.com.heliguard.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClimateRiskProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClimateRiskProfileDTO.class);
        ClimateRiskProfileDTO climateRiskProfileDTO1 = new ClimateRiskProfileDTO();
        climateRiskProfileDTO1.setId(1L);
        ClimateRiskProfileDTO climateRiskProfileDTO2 = new ClimateRiskProfileDTO();
        assertThat(climateRiskProfileDTO1).isNotEqualTo(climateRiskProfileDTO2);
        climateRiskProfileDTO2.setId(climateRiskProfileDTO1.getId());
        assertThat(climateRiskProfileDTO1).isEqualTo(climateRiskProfileDTO2);
        climateRiskProfileDTO2.setId(2L);
        assertThat(climateRiskProfileDTO1).isNotEqualTo(climateRiskProfileDTO2);
        climateRiskProfileDTO1.setId(null);
        assertThat(climateRiskProfileDTO1).isNotEqualTo(climateRiskProfileDTO2);
    }
}
