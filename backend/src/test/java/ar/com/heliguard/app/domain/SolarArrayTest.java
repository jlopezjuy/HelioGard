package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SolarArrayTestSamples.*;
import static ar.com.heliguard.app.domain.SolarSectorTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SolarArrayTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SolarArray.class);
        SolarArray solarArray1 = getSolarArraySample1();
        SolarArray solarArray2 = new SolarArray();
        assertThat(solarArray1).isNotEqualTo(solarArray2);

        solarArray2.setId(solarArray1.getId());
        assertThat(solarArray1).isEqualTo(solarArray2);

        solarArray2 = getSolarArraySample2();
        assertThat(solarArray1).isNotEqualTo(solarArray2);
    }

    @Test
    void organizationTest() {
        SolarArray solarArray = getSolarArrayRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        solarArray.setOrganization(organizationBack);
        assertThat(solarArray.getOrganization()).isEqualTo(organizationBack);

        solarArray.organization(null);
        assertThat(solarArray.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        SolarArray solarArray = getSolarArrayRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        solarArray.setInstallation(installationBack);
        assertThat(solarArray.getInstallation()).isEqualTo(installationBack);

        solarArray.installation(null);
        assertThat(solarArray.getInstallation()).isNull();
    }

    @Test
    void solarSectorTest() {
        SolarArray solarArray = getSolarArrayRandomSampleGenerator();
        SolarSector solarSectorBack = getSolarSectorRandomSampleGenerator();

        solarArray.setSolarSector(solarSectorBack);
        assertThat(solarArray.getSolarSector()).isEqualTo(solarSectorBack);

        solarArray.solarSector(null);
        assertThat(solarArray.getSolarSector()).isNull();
    }
}
