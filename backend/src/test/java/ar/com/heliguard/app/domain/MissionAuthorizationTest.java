package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DroneMissionTestSamples.*;
import static ar.com.heliguard.app.domain.MissionAuthorizationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MissionAuthorizationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MissionAuthorization.class);
        MissionAuthorization missionAuthorization1 = getMissionAuthorizationSample1();
        MissionAuthorization missionAuthorization2 = new MissionAuthorization();
        assertThat(missionAuthorization1).isNotEqualTo(missionAuthorization2);

        missionAuthorization2.setId(missionAuthorization1.getId());
        assertThat(missionAuthorization1).isEqualTo(missionAuthorization2);

        missionAuthorization2 = getMissionAuthorizationSample2();
        assertThat(missionAuthorization1).isNotEqualTo(missionAuthorization2);
    }

    @Test
    void organizationTest() {
        MissionAuthorization missionAuthorization = getMissionAuthorizationRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        missionAuthorization.setOrganization(organizationBack);
        assertThat(missionAuthorization.getOrganization()).isEqualTo(organizationBack);

        missionAuthorization.organization(null);
        assertThat(missionAuthorization.getOrganization()).isNull();
    }

    @Test
    void missionTest() {
        MissionAuthorization missionAuthorization = getMissionAuthorizationRandomSampleGenerator();
        DroneMission droneMissionBack = getDroneMissionRandomSampleGenerator();

        missionAuthorization.setMission(droneMissionBack);
        assertThat(missionAuthorization.getMission()).isEqualTo(droneMissionBack);

        missionAuthorization.mission(null);
        assertThat(missionAuthorization.getMission()).isNull();
    }

    @Test
    void authorizedByMembershipTest() {
        MissionAuthorization missionAuthorization = getMissionAuthorizationRandomSampleGenerator();
        OrganizationMembership organizationMembershipBack = getOrganizationMembershipRandomSampleGenerator();

        missionAuthorization.setAuthorizedByMembership(organizationMembershipBack);
        assertThat(missionAuthorization.getAuthorizedByMembership()).isEqualTo(organizationMembershipBack);

        missionAuthorization.authorizedByMembership(null);
        assertThat(missionAuthorization.getAuthorizedByMembership()).isNull();
    }
}
