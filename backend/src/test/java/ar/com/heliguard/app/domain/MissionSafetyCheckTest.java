package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DroneMissionTestSamples.*;
import static ar.com.heliguard.app.domain.MissionSafetyCheckTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MissionSafetyCheckTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MissionSafetyCheck.class);
        MissionSafetyCheck missionSafetyCheck1 = getMissionSafetyCheckSample1();
        MissionSafetyCheck missionSafetyCheck2 = new MissionSafetyCheck();
        assertThat(missionSafetyCheck1).isNotEqualTo(missionSafetyCheck2);

        missionSafetyCheck2.setId(missionSafetyCheck1.getId());
        assertThat(missionSafetyCheck1).isEqualTo(missionSafetyCheck2);

        missionSafetyCheck2 = getMissionSafetyCheckSample2();
        assertThat(missionSafetyCheck1).isNotEqualTo(missionSafetyCheck2);
    }

    @Test
    void organizationTest() {
        MissionSafetyCheck missionSafetyCheck = getMissionSafetyCheckRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        missionSafetyCheck.setOrganization(organizationBack);
        assertThat(missionSafetyCheck.getOrganization()).isEqualTo(organizationBack);

        missionSafetyCheck.organization(null);
        assertThat(missionSafetyCheck.getOrganization()).isNull();
    }

    @Test
    void missionTest() {
        MissionSafetyCheck missionSafetyCheck = getMissionSafetyCheckRandomSampleGenerator();
        DroneMission droneMissionBack = getDroneMissionRandomSampleGenerator();

        missionSafetyCheck.setMission(droneMissionBack);
        assertThat(missionSafetyCheck.getMission()).isEqualTo(droneMissionBack);

        missionSafetyCheck.mission(null);
        assertThat(missionSafetyCheck.getMission()).isNull();
    }

    @Test
    void checkedByMembershipTest() {
        MissionSafetyCheck missionSafetyCheck = getMissionSafetyCheckRandomSampleGenerator();
        OrganizationMembership organizationMembershipBack = getOrganizationMembershipRandomSampleGenerator();

        missionSafetyCheck.setCheckedByMembership(organizationMembershipBack);
        assertThat(missionSafetyCheck.getCheckedByMembership()).isEqualTo(organizationMembershipBack);

        missionSafetyCheck.checkedByMembership(null);
        assertThat(missionSafetyCheck.getCheckedByMembership()).isNull();
    }
}
