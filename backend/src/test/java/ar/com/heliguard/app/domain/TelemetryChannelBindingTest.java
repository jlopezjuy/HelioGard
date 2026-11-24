package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.GatewayTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.TelemetryChannelBindingTestSamples.*;
import static ar.com.heliguard.app.domain.TelemetryChannelTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TelemetryChannelBindingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelemetryChannelBinding.class);
        TelemetryChannelBinding telemetryChannelBinding1 = getTelemetryChannelBindingSample1();
        TelemetryChannelBinding telemetryChannelBinding2 = new TelemetryChannelBinding();
        assertThat(telemetryChannelBinding1).isNotEqualTo(telemetryChannelBinding2);

        telemetryChannelBinding2.setId(telemetryChannelBinding1.getId());
        assertThat(telemetryChannelBinding1).isEqualTo(telemetryChannelBinding2);

        telemetryChannelBinding2 = getTelemetryChannelBindingSample2();
        assertThat(telemetryChannelBinding1).isNotEqualTo(telemetryChannelBinding2);
    }

    @Test
    void organizationTest() {
        TelemetryChannelBinding telemetryChannelBinding = getTelemetryChannelBindingRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        telemetryChannelBinding.setOrganization(organizationBack);
        assertThat(telemetryChannelBinding.getOrganization()).isEqualTo(organizationBack);

        telemetryChannelBinding.organization(null);
        assertThat(telemetryChannelBinding.getOrganization()).isNull();
    }

    @Test
    void telemetryChannelTest() {
        TelemetryChannelBinding telemetryChannelBinding = getTelemetryChannelBindingRandomSampleGenerator();
        TelemetryChannel telemetryChannelBack = getTelemetryChannelRandomSampleGenerator();

        telemetryChannelBinding.setTelemetryChannel(telemetryChannelBack);
        assertThat(telemetryChannelBinding.getTelemetryChannel()).isEqualTo(telemetryChannelBack);

        telemetryChannelBinding.telemetryChannel(null);
        assertThat(telemetryChannelBinding.getTelemetryChannel()).isNull();
    }

    @Test
    void gatewayTest() {
        TelemetryChannelBinding telemetryChannelBinding = getTelemetryChannelBindingRandomSampleGenerator();
        Gateway gatewayBack = getGatewayRandomSampleGenerator();

        telemetryChannelBinding.setGateway(gatewayBack);
        assertThat(telemetryChannelBinding.getGateway()).isEqualTo(gatewayBack);

        telemetryChannelBinding.gateway(null);
        assertThat(telemetryChannelBinding.getGateway()).isNull();
    }
}
