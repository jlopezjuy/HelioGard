package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DroneMissionTargetTestSamples.*;
import static ar.com.heliguard.app.domain.DroneMissionTestSamples.*;
import static ar.com.heliguard.app.domain.EquipmentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SolarSectorTestSamples.*;
import static ar.com.heliguard.app.domain.SolarStringTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DroneMissionTargetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DroneMissionTarget.class);
        DroneMissionTarget droneMissionTarget1 = getDroneMissionTargetSample1();
        DroneMissionTarget droneMissionTarget2 = new DroneMissionTarget();
        assertThat(droneMissionTarget1).isNotEqualTo(droneMissionTarget2);

        droneMissionTarget2.setId(droneMissionTarget1.getId());
        assertThat(droneMissionTarget1).isEqualTo(droneMissionTarget2);

        droneMissionTarget2 = getDroneMissionTargetSample2();
        assertThat(droneMissionTarget1).isNotEqualTo(droneMissionTarget2);
    }

    @Test
    void organizationTest() {
        DroneMissionTarget droneMissionTarget = getDroneMissionTargetRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        droneMissionTarget.setOrganization(organizationBack);
        assertThat(droneMissionTarget.getOrganization()).isEqualTo(organizationBack);

        droneMissionTarget.organization(null);
        assertThat(droneMissionTarget.getOrganization()).isNull();
    }

    @Test
    void missionTest() {
        DroneMissionTarget droneMissionTarget = getDroneMissionTargetRandomSampleGenerator();
        DroneMission droneMissionBack = getDroneMissionRandomSampleGenerator();

        droneMissionTarget.setMission(droneMissionBack);
        assertThat(droneMissionTarget.getMission()).isEqualTo(droneMissionBack);

        droneMissionTarget.mission(null);
        assertThat(droneMissionTarget.getMission()).isNull();
    }

    @Test
    void solarSectorTest() {
        DroneMissionTarget droneMissionTarget = getDroneMissionTargetRandomSampleGenerator();
        SolarSector solarSectorBack = getSolarSectorRandomSampleGenerator();

        droneMissionTarget.setSolarSector(solarSectorBack);
        assertThat(droneMissionTarget.getSolarSector()).isEqualTo(solarSectorBack);

        droneMissionTarget.solarSector(null);
        assertThat(droneMissionTarget.getSolarSector()).isNull();
    }

    @Test
    void solarStringTest() {
        DroneMissionTarget droneMissionTarget = getDroneMissionTargetRandomSampleGenerator();
        SolarString solarStringBack = getSolarStringRandomSampleGenerator();

        droneMissionTarget.setSolarString(solarStringBack);
        assertThat(droneMissionTarget.getSolarString()).isEqualTo(solarStringBack);

        droneMissionTarget.solarString(null);
        assertThat(droneMissionTarget.getSolarString()).isNull();
    }

    @Test
    void equipmentTest() {
        DroneMissionTarget droneMissionTarget = getDroneMissionTargetRandomSampleGenerator();
        Equipment equipmentBack = getEquipmentRandomSampleGenerator();

        droneMissionTarget.setEquipment(equipmentBack);
        assertThat(droneMissionTarget.getEquipment()).isEqualTo(equipmentBack);

        droneMissionTarget.equipment(null);
        assertThat(droneMissionTarget.getEquipment()).isNull();
    }
}
