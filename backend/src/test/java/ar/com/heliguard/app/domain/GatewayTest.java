package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.GatewayTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GatewayTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Gateway.class);
        Gateway gateway1 = getGatewaySample1();
        Gateway gateway2 = new Gateway();
        assertThat(gateway1).isNotEqualTo(gateway2);

        gateway2.setId(gateway1.getId());
        assertThat(gateway1).isEqualTo(gateway2);

        gateway2 = getGatewaySample2();
        assertThat(gateway1).isNotEqualTo(gateway2);
    }

    @Test
    void organizationTest() {
        Gateway gateway = getGatewayRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        gateway.setOrganization(organizationBack);
        assertThat(gateway.getOrganization()).isEqualTo(organizationBack);

        gateway.organization(null);
        assertThat(gateway.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        Gateway gateway = getGatewayRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        gateway.setInstallation(installationBack);
        assertThat(gateway.getInstallation()).isEqualTo(installationBack);

        gateway.installation(null);
        assertThat(gateway.getInstallation()).isNull();
    }
}
