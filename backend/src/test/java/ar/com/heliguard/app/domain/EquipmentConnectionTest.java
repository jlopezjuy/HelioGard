package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EquipmentConnectionTestSamples.*;
import static ar.com.heliguard.app.domain.EquipmentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipmentConnectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EquipmentConnection.class);
        EquipmentConnection equipmentConnection1 = getEquipmentConnectionSample1();
        EquipmentConnection equipmentConnection2 = new EquipmentConnection();
        assertThat(equipmentConnection1).isNotEqualTo(equipmentConnection2);

        equipmentConnection2.setId(equipmentConnection1.getId());
        assertThat(equipmentConnection1).isEqualTo(equipmentConnection2);

        equipmentConnection2 = getEquipmentConnectionSample2();
        assertThat(equipmentConnection1).isNotEqualTo(equipmentConnection2);
    }

    @Test
    void organizationTest() {
        EquipmentConnection equipmentConnection = getEquipmentConnectionRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        equipmentConnection.setOrganization(organizationBack);
        assertThat(equipmentConnection.getOrganization()).isEqualTo(organizationBack);

        equipmentConnection.organization(null);
        assertThat(equipmentConnection.getOrganization()).isNull();
    }

    @Test
    void fromEquipmentTest() {
        EquipmentConnection equipmentConnection = getEquipmentConnectionRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        equipmentConnection.setFromEquipment(equipmentBack);
        assertThat(equipmentConnection.getFromEquipment()).isEqualTo(equipmentBack);

        equipmentConnection.fromEquipment(null);
        assertThat(equipmentConnection.getFromEquipment()).isNull();
    }

    @Test
    void toEquipmentTest() {
        EquipmentConnection equipmentConnection = getEquipmentConnectionRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        equipmentConnection.setToEquipment(equipmentBack);
        assertThat(equipmentConnection.getToEquipment()).isEqualTo(equipmentBack);

        equipmentConnection.toEquipment(null);
        assertThat(equipmentConnection.getToEquipment()).isNull();
    }
}
