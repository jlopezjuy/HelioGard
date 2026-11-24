package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.IntegrityAssessmentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.SecurityIncidentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SecurityIncidentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SecurityIncident.class);
        SecurityIncident securityIncident1 = getSecurityIncidentSample1();
        SecurityIncident securityIncident2 = new SecurityIncident();
        assertThat(securityIncident1).isNotEqualTo(securityIncident2);

        securityIncident2.setId(securityIncident1.getId());
        assertThat(securityIncident1).isEqualTo(securityIncident2);

        securityIncident2 = getSecurityIncidentSample2();
        assertThat(securityIncident1).isNotEqualTo(securityIncident2);
    }

    @Test
    void organizationTest() {
        SecurityIncident securityIncident = getSecurityIncidentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        securityIncident.setOrganization(organizationBack);
        assertThat(securityIncident.getOrganization()).isEqualTo(organizationBack);

        securityIncident.organization(null);
        assertThat(securityIncident.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        SecurityIncident securityIncident = getSecurityIncidentRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        securityIncident.setInstallation(installationBack);
        assertThat(securityIncident.getInstallation()).isEqualTo(installationBack);

        securityIncident.installation(null);
        assertThat(securityIncident.getInstallation()).isNull();
    }

    @Test
    void integrityAssessmentTest() {
        SecurityIncident securityIncident = getSecurityIncidentRandomSampleGenerator();
        IntegrityAssessment integrityAssessmentBack = getIntegrityAssessmentRandomSampleGenerator();

        securityIncident.setIntegrityAssessment(integrityAssessmentBack);
        assertThat(securityIncident.getIntegrityAssessment()).isEqualTo(integrityAssessmentBack);

        securityIncident.integrityAssessment(null);
        assertThat(securityIncident.getIntegrityAssessment()).isNull();
    }
}
