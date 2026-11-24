package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EnergyModelConfigurationTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EnergyModelConfigurationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EnergyModelConfiguration.class);
        EnergyModelConfiguration energyModelConfiguration1 = getEnergyModelConfigurationSample1();
        EnergyModelConfiguration energyModelConfiguration2 = new EnergyModelConfiguration();
        assertThat(energyModelConfiguration1).isNotEqualTo(energyModelConfiguration2);

        energyModelConfiguration2.setId(energyModelConfiguration1.getId());
        assertThat(energyModelConfiguration1).isEqualTo(energyModelConfiguration2);

        energyModelConfiguration2 = getEnergyModelConfigurationSample2();
        assertThat(energyModelConfiguration1).isNotEqualTo(energyModelConfiguration2);
    }

    @Test
    void organizationTest() {
        EnergyModelConfiguration energyModelConfiguration = getEnergyModelConfigurationRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        energyModelConfiguration.setOrganization(organizationBack);
        assertThat(energyModelConfiguration.getOrganization()).isEqualTo(organizationBack);

        energyModelConfiguration.organization(null);
        assertThat(energyModelConfiguration.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        EnergyModelConfiguration energyModelConfiguration = getEnergyModelConfigurationRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        energyModelConfiguration.setInstallation(installationBack);
        assertThat(energyModelConfiguration.getInstallation()).isEqualTo(installationBack);

        energyModelConfiguration.installation(null);
        assertThat(energyModelConfiguration.getInstallation()).isNull();
    }
}
