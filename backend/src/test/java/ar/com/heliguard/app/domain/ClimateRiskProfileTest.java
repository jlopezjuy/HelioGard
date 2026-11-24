package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.ClimateRiskProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ClimateRiskProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClimateRiskProfile.class);
        ClimateRiskProfile climateRiskProfile1 = getClimateRiskProfileSample1();
        ClimateRiskProfile climateRiskProfile2 = new ClimateRiskProfile();
        assertThat(climateRiskProfile1).isNotEqualTo(climateRiskProfile2);

        climateRiskProfile2.setId(climateRiskProfile1.getId());
        assertThat(climateRiskProfile1).isEqualTo(climateRiskProfile2);

        climateRiskProfile2 = getClimateRiskProfileSample2();
        assertThat(climateRiskProfile1).isNotEqualTo(climateRiskProfile2);
    }
}
