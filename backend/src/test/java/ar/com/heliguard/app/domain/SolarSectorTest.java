package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SolarSectorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolarSectorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolarSector.class);
        SolarSector solarSector1 = getSolarSectorSample1();
        SolarSector solarSector2 = new SolarSector();
        assertThat(solarSector1).isNotEqualTo(solarSector2);

        solarSector2.setId(solarSector1.getId());
        assertThat(solarSector1).isEqualTo(solarSector2);

        solarSector2 = getSolarSectorSample2();
        assertThat(solarSector1).isNotEqualTo(solarSector2);
    }

    @Test
    void organizationTest() {
        SolarSector solarSector = getSolarSectorRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        solarSector.setOrganization(organizationBack);
        assertThat(solarSector.getOrganization()).isEqualTo(organizationBack);

        solarSector.organization(null);
        assertThat(solarSector.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        SolarSector solarSector = getSolarSectorRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        solarSector.setInstallation(installationBack);
        assertThat(solarSector.getInstallation()).isEqualTo(installationBack);

        solarSector.installation(null);
        assertThat(solarSector.getInstallation()).isNull();
    }
}
