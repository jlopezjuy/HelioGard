package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.TariffPlanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TariffPlanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TariffPlan.class);
        TariffPlan tariffPlan1 = getTariffPlanSample1();
        TariffPlan tariffPlan2 = new TariffPlan();
        assertThat(tariffPlan1).isNotEqualTo(tariffPlan2);

        tariffPlan2.setId(tariffPlan1.getId());
        assertThat(tariffPlan1).isEqualTo(tariffPlan2);

        tariffPlan2 = getTariffPlanSample2();
        assertThat(tariffPlan1).isNotEqualTo(tariffPlan2);
    }

    @Test
    void organizationTest() {
        TariffPlan tariffPlan = getTariffPlanRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        tariffPlan.setOrganization(organizationBack);
        assertThat(tariffPlan.getOrganization()).isEqualTo(organizationBack);

        tariffPlan.organization(null);
        assertThat(tariffPlan.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        TariffPlan tariffPlan = getTariffPlanRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        tariffPlan.setInstallation(installationBack);
        assertThat(tariffPlan.getInstallation()).isEqualTo(installationBack);

        tariffPlan.installation(null);
        assertThat(tariffPlan.getInstallation()).isNull();
    }
}
