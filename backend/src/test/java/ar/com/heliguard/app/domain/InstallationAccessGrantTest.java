package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InstallationAccessGrantTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InstallationAccessGrantTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstallationAccessGrant.class);
        InstallationAccessGrant installationAccessGrant1 = getInstallationAccessGrantSample1();
        InstallationAccessGrant installationAccessGrant2 = new InstallationAccessGrant();
        assertThat(installationAccessGrant1).isNotEqualTo(installationAccessGrant2);

        installationAccessGrant2.setId(installationAccessGrant1.getId());
        assertThat(installationAccessGrant1).isEqualTo(installationAccessGrant2);

        installationAccessGrant2 = getInstallationAccessGrantSample2();
        assertThat(installationAccessGrant1).isNotEqualTo(installationAccessGrant2);
    }

    @Test
    void organizationTest() {
        InstallationAccessGrant installationAccessGrant = getInstallationAccessGrantRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        installationAccessGrant.setOrganization(organizationBack);
        assertThat(installationAccessGrant.getOrganization()).isEqualTo(organizationBack);

        installationAccessGrant.organization(null);
        assertThat(installationAccessGrant.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        InstallationAccessGrant installationAccessGrant = getInstallationAccessGrantRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        installationAccessGrant.setInstallation(installationBack);
        assertThat(installationAccessGrant.getInstallation()).isEqualTo(installationBack);

        installationAccessGrant.installation(null);
        assertThat(installationAccessGrant.getInstallation()).isNull();
    }

    @Test
    void granteeOrganizationTest() {
        InstallationAccessGrant installationAccessGrant = getInstallationAccessGrantRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        installationAccessGrant.setGranteeOrganization(organizationBack);
        assertThat(installationAccessGrant.getGranteeOrganization()).isEqualTo(organizationBack);

        installationAccessGrant.granteeOrganization(null);
        assertThat(installationAccessGrant.getGranteeOrganization()).isNull();
    }
}
