package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.AlertRuleVersionTestSamples.*;
import static ar.com.heliguard.app.domain.IncidentTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IncidentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Incident.class);
        Incident incident1 = getIncidentSample1();
        Incident incident2 = new Incident();
        assertThat(incident1).isNotEqualTo(incident2);

        incident2.setId(incident1.getId());
        assertThat(incident1).isEqualTo(incident2);

        incident2 = getIncidentSample2();
        assertThat(incident1).isNotEqualTo(incident2);
    }

    @Test
    void organizationTest() {
        Incident incident = getIncidentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        incident.setOrganization(organizationBack);
        assertThat(incident.getOrganization()).isEqualTo(organizationBack);

        incident.organization(null);
        assertThat(incident.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        Incident incident = getIncidentRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        incident.setInstallation(installationBack);
        assertThat(incident.getInstallation()).isEqualTo(installationBack);

        incident.installation(null);
        assertThat(incident.getInstallation()).isNull();
    }

    @Test
    void alertRuleVersionTest() {
        Incident incident = getIncidentRandomSampleGenerator();
        AlertRuleVersion alertRuleVersionBack = getAlertRuleVersionRandomSampleGenerator();

        incident.setAlertRuleVersion(alertRuleVersionBack);
        assertThat(incident.getAlertRuleVersion()).isEqualTo(alertRuleVersionBack);

        incident.alertRuleVersion(null);
        assertThat(incident.getAlertRuleVersion()).isNull();
    }
}
