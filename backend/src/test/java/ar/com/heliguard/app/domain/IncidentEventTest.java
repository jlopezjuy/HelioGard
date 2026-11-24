package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.IncidentEventTestSamples.*;
import static ar.com.heliguard.app.domain.IncidentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IncidentEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IncidentEvent.class);
        IncidentEvent incidentEvent1 = getIncidentEventSample1();
        IncidentEvent incidentEvent2 = new IncidentEvent();
        assertThat(incidentEvent1).isNotEqualTo(incidentEvent2);

        incidentEvent2.setId(incidentEvent1.getId());
        assertThat(incidentEvent1).isEqualTo(incidentEvent2);

        incidentEvent2 = getIncidentEventSample2();
        assertThat(incidentEvent1).isNotEqualTo(incidentEvent2);
    }

    @Test
    void organizationTest() {
        IncidentEvent incidentEvent = getIncidentEventRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        incidentEvent.setOrganization(organizationBack);
        assertThat(incidentEvent.getOrganization()).isEqualTo(organizationBack);

        incidentEvent.organization(null);
        assertThat(incidentEvent.getOrganization()).isNull();
    }

    @Test
    void incidentTest() {
        IncidentEvent incidentEvent = getIncidentEventRandomSampleGenerator();
        Incident incidentBack = getIncidentRandomSampleGenerator();

        incidentEvent.setIncident(incidentBack);
        assertThat(incidentEvent.getIncident()).isEqualTo(incidentBack);

        incidentEvent.incident(null);
        assertThat(incidentEvent.getIncident()).isNull();
    }
}
