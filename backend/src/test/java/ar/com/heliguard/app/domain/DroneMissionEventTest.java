package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DroneMissionEventTestSamples.*;
import static ar.com.heliguard.app.domain.DroneMissionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DroneMissionEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DroneMissionEvent.class);
        DroneMissionEvent droneMissionEvent1 = getDroneMissionEventSample1();
        DroneMissionEvent droneMissionEvent2 = new DroneMissionEvent();
        assertThat(droneMissionEvent1).isNotEqualTo(droneMissionEvent2);

        droneMissionEvent2.setId(droneMissionEvent1.getId());
        assertThat(droneMissionEvent1).isEqualTo(droneMissionEvent2);

        droneMissionEvent2 = getDroneMissionEventSample2();
        assertThat(droneMissionEvent1).isNotEqualTo(droneMissionEvent2);
    }

    @Test
    void organizationTest() {
        DroneMissionEvent droneMissionEvent = getDroneMissionEventRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        droneMissionEvent.setOrganization(organizationBack);
        assertThat(droneMissionEvent.getOrganization()).isEqualTo(organizationBack);

        droneMissionEvent.organization(null);
        assertThat(droneMissionEvent.getOrganization()).isNull();
    }

    @Test
    void missionTest() {
        DroneMissionEvent droneMissionEvent = getDroneMissionEventRandomSampleGenerator();
        DroneMission droneMissionBack = getDroneMissionRandomSampleGenerator();

        droneMissionEvent.setMission(droneMissionBack);
        assertThat(droneMissionEvent.getMission()).isEqualTo(droneMissionBack);

        droneMissionEvent.mission(null);
        assertThat(droneMissionEvent.getMission()).isNull();
    }

    @Test
    void operatorMembershipTest() {
        DroneMissionEvent droneMissionEvent = getDroneMissionEventRandomSampleGenerator();
        OrganizationMembership organizationMembershipBack = getOrganizationMembershipRandomSampleGenerator();

        droneMissionEvent.setOperatorMembership(organizationMembershipBack);
        assertThat(droneMissionEvent.getOperatorMembership()).isEqualTo(organizationMembershipBack);

        droneMissionEvent.operatorMembership(null);
        assertThat(droneMissionEvent.getOperatorMembership()).isNull();
    }
}
