package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.OrganizationMembershipTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SecurityCaseEventTestSamples.*;
import static ar.com.heliguard.app.domain.SecurityIncidentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SecurityCaseEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SecurityCaseEvent.class);
        SecurityCaseEvent securityCaseEvent1 = getSecurityCaseEventSample1();
        SecurityCaseEvent securityCaseEvent2 = new SecurityCaseEvent();
        assertThat(securityCaseEvent1).isNotEqualTo(securityCaseEvent2);

        securityCaseEvent2.setId(securityCaseEvent1.getId());
        assertThat(securityCaseEvent1).isEqualTo(securityCaseEvent2);

        securityCaseEvent2 = getSecurityCaseEventSample2();
        assertThat(securityCaseEvent1).isNotEqualTo(securityCaseEvent2);
    }

    @Test
    void organizationTest() {
        SecurityCaseEvent securityCaseEvent = getSecurityCaseEventRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        securityCaseEvent.setOrganization(organizationBack);
        assertThat(securityCaseEvent.getOrganization()).isEqualTo(organizationBack);

        securityCaseEvent.organization(null);
        assertThat(securityCaseEvent.getOrganization()).isNull();
    }

    @Test
    void securityIncidentTest() {
        SecurityCaseEvent securityCaseEvent = getSecurityCaseEventRandomSampleGenerator();
        SecurityIncident securityIncidentBack = getSecurityIncidentRandomSampleGenerator();

        securityCaseEvent.setSecurityIncident(securityIncidentBack);
        assertThat(securityCaseEvent.getSecurityIncident()).isEqualTo(securityIncidentBack);

        securityCaseEvent.securityIncident(null);
        assertThat(securityCaseEvent.getSecurityIncident()).isNull();
    }

    @Test
    void actorMembershipTest() {
        SecurityCaseEvent securityCaseEvent = getSecurityCaseEventRandomSampleGenerator();
        OrganizationMembership organizationMembershipBack = getOrganizationMembershipRandomSampleGenerator();

        securityCaseEvent.setActorMembership(organizationMembershipBack);
        assertThat(securityCaseEvent.getActorMembership()).isEqualTo(organizationMembershipBack);

        securityCaseEvent.actorMembership(null);
        assertThat(securityCaseEvent.getActorMembership()).isNull();
    }
}
