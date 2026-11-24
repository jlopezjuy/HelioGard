package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EquipmentTestSamples.*;
import static ar.com.heliguard.app.domain.MetricDefinitionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.TelemetryChannelTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TelemetryChannelTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TelemetryChannel.class);
        TelemetryChannel telemetryChannel1 = getTelemetryChannelSample1();
        TelemetryChannel telemetryChannel2 = new TelemetryChannel();
        assertThat(telemetryChannel1).isNotEqualTo(telemetryChannel2);

        telemetryChannel2.setId(telemetryChannel1.getId());
        assertThat(telemetryChannel1).isEqualTo(telemetryChannel2);

        telemetryChannel2 = getTelemetryChannelSample2();
        assertThat(telemetryChannel1).isNotEqualTo(telemetryChannel2);
    }

    @Test
    void organizationTest() {
        TelemetryChannel telemetryChannel = getTelemetryChannelRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        telemetryChannel.setOrganization(organizationBack);
        assertThat(telemetryChannel.getOrganization()).isEqualTo(organizationBack);

        telemetryChannel.organization(null);
        assertThat(telemetryChannel.getOrganization()).isNull();
    }

    @Test
    void equipmentTest() {
        TelemetryChannel telemetryChannel = getTelemetryChannelRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        telemetryChannel.setEquipment(equipmentBack);
        assertThat(telemetryChannel.getEquipment()).isEqualTo(equipmentBack);

        telemetryChannel.equipment(null);
        assertThat(telemetryChannel.getEquipment()).isNull();
    }

    @Test
    void metricDefinitionTest() {
        TelemetryChannel telemetryChannel = getTelemetryChannelRandomSampleGenerator();
        MetricDefinition metricDefinitionBack = getMetricDefinitionRandomSampleGenerator();

        telemetryChannel.setMetricDefinition(metricDefinitionBack);
        assertThat(telemetryChannel.getMetricDefinition()).isEqualTo(metricDefinitionBack);

        telemetryChannel.metricDefinition(null);
        assertThat(telemetryChannel.getMetricDefinition()).isNull();
    }
}
