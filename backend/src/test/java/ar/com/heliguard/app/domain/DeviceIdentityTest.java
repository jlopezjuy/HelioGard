package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DeviceIdentityTestSamples.*;
import static ar.com.heliguard.app.domain.EquipmentTestSamples.*;
import static ar.com.heliguard.app.domain.GatewayTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DeviceIdentityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DeviceIdentity.class);
        DeviceIdentity deviceIdentity1 = getDeviceIdentitySample1();
        DeviceIdentity deviceIdentity2 = new DeviceIdentity();
        assertThat(deviceIdentity1).isNotEqualTo(deviceIdentity2);

        deviceIdentity2.setId(deviceIdentity1.getId());
        assertThat(deviceIdentity1).isEqualTo(deviceIdentity2);

        deviceIdentity2 = getDeviceIdentitySample2();
        assertThat(deviceIdentity1).isNotEqualTo(deviceIdentity2);
    }

    @Test
    void organizationTest() {
        DeviceIdentity deviceIdentity = getDeviceIdentityRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        deviceIdentity.setOrganization(organizationBack);
        assertThat(deviceIdentity.getOrganization()).isEqualTo(organizationBack);

        deviceIdentity.organization(null);
        assertThat(deviceIdentity.getOrganization()).isNull();
    }

    @Test
    void gatewayTest() {
        DeviceIdentity deviceIdentity = getDeviceIdentityRandomSampleGenerator();
        Gateway gatewayBack = getGatewayRandomSampleGenerator();

        deviceIdentity.setGateway(gatewayBack);
        assertThat(deviceIdentity.getGateway()).isEqualTo(gatewayBack);

        deviceIdentity.gateway(null);
        assertThat(deviceIdentity.getGateway()).isNull();
    }

    @Test
    void equipmentTest() {
        DeviceIdentity deviceIdentity = getDeviceIdentityRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        deviceIdentity.setEquipment(equipmentBack);
        assertThat(deviceIdentity.getEquipment()).isEqualTo(equipmentBack);

        deviceIdentity.equipment(null);
        assertThat(deviceIdentity.getEquipment()).isNull();
    }
}
