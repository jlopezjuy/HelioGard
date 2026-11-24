package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.TariffPlanTestSamples.*;
import static ar.com.heliguard.app.domain.TariffRateTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TariffRateTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TariffRate.class);
        TariffRate tariffRate1 = getTariffRateSample1();
        TariffRate tariffRate2 = new TariffRate();
        assertThat(tariffRate1).isNotEqualTo(tariffRate2);

        tariffRate2.setId(tariffRate1.getId());
        assertThat(tariffRate1).isEqualTo(tariffRate2);

        tariffRate2 = getTariffRateSample2();
        assertThat(tariffRate1).isNotEqualTo(tariffRate2);
    }

    @Test
    void organizationTest() {
        TariffRate tariffRate = getTariffRateRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        tariffRate.setOrganization(organizationBack);
        assertThat(tariffRate.getOrganization()).isEqualTo(organizationBack);

        tariffRate.organization(null);
        assertThat(tariffRate.getOrganization()).isNull();
    }

    @Test
    void tariffPlanTest() {
        TariffRate tariffRate = getTariffRateRandomSampleGenerator();
        TariffPlan tariffPlanBack = getTariffPlanRandomSampleGenerator();

        tariffRate.setTariffPlan(tariffPlanBack);
        assertThat(tariffRate.getTariffPlan()).isEqualTo(tariffPlanBack);

        tariffRate.tariffPlan(null);
        assertThat(tariffRate.getTariffPlan()).isNull();
    }
}
