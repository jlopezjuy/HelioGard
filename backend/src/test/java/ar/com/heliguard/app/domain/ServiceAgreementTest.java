package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.ServiceAgreementTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ServiceAgreementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ServiceAgreement.class);
        ServiceAgreement serviceAgreement1 = getServiceAgreementSample1();
        ServiceAgreement serviceAgreement2 = new ServiceAgreement();
        assertThat(serviceAgreement1).isNotEqualTo(serviceAgreement2);

        serviceAgreement2.setId(serviceAgreement1.getId());
        assertThat(serviceAgreement1).isEqualTo(serviceAgreement2);

        serviceAgreement2 = getServiceAgreementSample2();
        assertThat(serviceAgreement1).isNotEqualTo(serviceAgreement2);
    }

    @Test
    void organizationTest() {
        ServiceAgreement serviceAgreement = getServiceAgreementRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        serviceAgreement.setOrganization(organizationBack);
        assertThat(serviceAgreement.getOrganization()).isEqualTo(organizationBack);

        serviceAgreement.organization(null);
        assertThat(serviceAgreement.getOrganization()).isNull();
    }

    @Test
    void providerOrganizationTest() {
        ServiceAgreement serviceAgreement = getServiceAgreementRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        serviceAgreement.setProviderOrganization(organizationBack);
        assertThat(serviceAgreement.getProviderOrganization()).isEqualTo(organizationBack);

        serviceAgreement.providerOrganization(null);
        assertThat(serviceAgreement.getProviderOrganization()).isNull();
    }

    @Test
    void installationTest() {
        ServiceAgreement serviceAgreement = getServiceAgreementRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        serviceAgreement.setInstallation(installationBack);
        assertThat(serviceAgreement.getInstallation()).isEqualTo(installationBack);

        serviceAgreement.installation(null);
        assertThat(serviceAgreement.getInstallation()).isNull();
    }
}
