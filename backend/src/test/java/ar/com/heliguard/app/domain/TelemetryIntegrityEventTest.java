package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DeviceIdentityTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.TelemetryChannelTestSamples.*;
import static ar.com.heliguard.app.domain.TelemetryIntegrityEventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TelemetryIntegrityEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelemetryIntegrityEvent.class);
        TelemetryIntegrityEvent telemetryIntegrityEvent1 = getTelemetryIntegrityEventSample1();
        TelemetryIntegrityEvent telemetryIntegrityEvent2 = new TelemetryIntegrityEvent();
        assertThat(telemetryIntegrityEvent1).isNotEqualTo(telemetryIntegrityEvent2);

        telemetryIntegrityEvent2.setId(telemetryIntegrityEvent1.getId());
        assertThat(telemetryIntegrityEvent1).isEqualTo(telemetryIntegrityEvent2);

        telemetryIntegrityEvent2 = getTelemetryIntegrityEventSample2();
        assertThat(telemetryIntegrityEvent1).isNotEqualTo(telemetryIntegrityEvent2);
    }

    @Test
    void organizationTest() {
        TelemetryIntegrityEvent telemetryIntegrityEvent = getTelemetryIntegrityEventRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        telemetryIntegrityEvent.setOrganization(organizationBack);
        assertThat(telemetryIntegrityEvent.getOrganization()).isEqualTo(organizationBack);

        telemetryIntegrityEvent.organization(null);
        assertThat(telemetryIntegrityEvent.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        TelemetryIntegrityEvent telemetryIntegrityEvent = getTelemetryIntegrityEventRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        telemetryIntegrityEvent.setInstallation(installationBack);
        assertThat(telemetryIntegrityEvent.getInstallation()).isEqualTo(installationBack);

        telemetryIntegrityEvent.installation(null);
        assertThat(telemetryIntegrityEvent.getInstallation()).isNull();
    }

    @Test
    void deviceIdentityTest() {
        TelemetryIntegrityEvent telemetryIntegrityEvent = getTelemetryIntegrityEventRandomSampleGenerator();
        DeviceIdentity deviceIdentityBack = getDeviceIdentityRandomSampleGenerator();

        telemetryIntegrityEvent.setDeviceIdentity(deviceIdentityBack);
        assertThat(telemetryIntegrityEvent.getDeviceIdentity()).isEqualTo(deviceIdentityBack);

        telemetryIntegrityEvent.deviceIdentity(null);
        assertThat(telemetryIntegrityEvent.getDeviceIdentity()).isNull();
    }

    @Test
    void telemetryChannelTest() {
        TelemetryIntegrityEvent telemetryIntegrityEvent = getTelemetryIntegrityEventRandomSampleGenerator();
        TelemetryChannel telemetryChannelBack = getTelemetryChannelRandomSampleGenerator();

        telemetryIntegrityEvent.setTelemetryChannel(telemetryChannelBack);
        assertThat(telemetryIntegrityEvent.getTelemetryChannel()).isEqualTo(telemetryChannelBack);

        telemetryIntegrityEvent.telemetryChannel(null);
        assertThat(telemetryIntegrityEvent.getTelemetryChannel()).isNull();
    }
}
