package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.ClimateRiskProfileTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SiteTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstallationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Installation.class);
        Installation installation1 = getInstallationSample1();
        Installation installation2 = new Installation();
        assertThat(installation1).isNotEqualTo(installation2);

        installation2.setId(installation1.getId());
        assertThat(installation1).isEqualTo(installation2);

        installation2 = getInstallationSample2();
        assertThat(installation1).isNotEqualTo(installation2);
    }

    @Test
    void organizationTest() {
        Installation installation = getInstallationRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        installation.setOrganization(organizationBack);
        assertThat(installation.getOrganization()).isEqualTo(organizationBack);

        installation.organization(null);
        assertThat(installation.getOrganization()).isNull();
    }

    @Test
    void siteTest() {
        Installation installation = getInstallationRandomSampleGenerator();
        Site siteBack = getSiteRandomSampleGenerator();

        installation.setSite(siteBack);
        assertThat(installation.getSite()).isEqualTo(siteBack);

        installation.site(null);
        assertThat(installation.getSite()).isNull();
    }

    @Test
    void climateRiskProfileTest() {
        Installation installation = getInstallationRandomSampleGenerator();
        ClimateRiskProfile climateRiskProfileBack = getClimateRiskProfileRandomSampleGenerator();

        installation.setClimateRiskProfile(climateRiskProfileBack);
        assertThat(installation.getClimateRiskProfile()).isEqualTo(climateRiskProfileBack);

        installation.climateRiskProfile(null);
        assertThat(installation.getClimateRiskProfile()).isNull();
    }
}
