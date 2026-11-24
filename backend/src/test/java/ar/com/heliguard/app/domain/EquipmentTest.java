package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EquipmentModelTestSamples.*;
import static ar.com.heliguard.app.domain.EquipmentTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SolarStringTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EquipmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Equipment.class);
        Equipment equipment1 = getEquipmentSample1();
        Equipment equipment2 = new Equipment();
        assertThat(equipment1).isNotEqualTo(equipment2);

        equipment2.setId(equipment1.getId());
        assertThat(equipment1).isEqualTo(equipment2);

        equipment2 = getEquipmentSample2();
        assertThat(equipment1).isNotEqualTo(equipment2);
    }

    @Test
    void organizationTest() {
        Equipment equipment = getEquipmentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        equipment.setOrganization(organizationBack);
        assertThat(equipment.getOrganization()).isEqualTo(organizationBack);

        equipment.organization(null);
        assertThat(equipment.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        Equipment equipment = getEquipmentRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        equipment.setInstallation(installationBack);
        assertThat(equipment.getInstallation()).isEqualTo(installationBack);

        equipment.installation(null);
        assertThat(equipment.getInstallation()).isNull();
    }

    @Test
    void equipmentModelTest() {
        Equipment equipment = getEquipmentRandomSampleGenerator();
        EquipmentModel equipmentModelBack = getEquipmentModelRandomSampleGenerator();

        equipment.setEquipmentModel(equipmentModelBack);
        assertThat(equipment.getEquipmentModel()).isEqualTo(equipmentModelBack);

        equipment.equipmentModel(null);
        assertThat(equipment.getEquipmentModel()).isNull();
    }

    @Test
    void solarStringTest() {
        Equipment equipment = getEquipmentRandomSampleGenerator();
        SolarString solarStringBack = getSolarStringRandomSampleGenerator();

        equipment.setSolarString(solarStringBack);
        assertThat(equipment.getSolarString()).isEqualTo(solarStringBack);

        equipment.solarString(null);
        assertThat(equipment.getSolarString()).isNull();
    }
}
