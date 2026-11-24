package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EnergyModelRunTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.IntegrityAssessmentTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.TelemetryIntegrityEventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class IntegrityAssessmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(IntegrityAssessment.class);
        IntegrityAssessment integrityAssessment1 = getIntegrityAssessmentSample1();
        IntegrityAssessment integrityAssessment2 = new IntegrityAssessment();
        assertThat(integrityAssessment1).isNotEqualTo(integrityAssessment2);

        integrityAssessment2.setId(integrityAssessment1.getId());
        assertThat(integrityAssessment1).isEqualTo(integrityAssessment2);

        integrityAssessment2 = getIntegrityAssessmentSample2();
        assertThat(integrityAssessment1).isNotEqualTo(integrityAssessment2);
    }

    @Test
    void organizationTest() {
        IntegrityAssessment integrityAssessment = getIntegrityAssessmentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        integrityAssessment.setOrganization(organizationBack);
        assertThat(integrityAssessment.getOrganization()).isEqualTo(organizationBack);

        integrityAssessment.organization(null);
        assertThat(integrityAssessment.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        IntegrityAssessment integrityAssessment = getIntegrityAssessmentRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        integrityAssessment.setInstallation(installationBack);
        assertThat(integrityAssessment.getInstallation()).isEqualTo(installationBack);

        integrityAssessment.installation(null);
        assertThat(integrityAssessment.getInstallation()).isNull();
    }

    @Test
    void integrityEventTest() {
        IntegrityAssessment integrityAssessment = getIntegrityAssessmentRandomSampleGenerator();
        TelemetryIntegrityEvent telemetryIntegrityEventBack = getTelemetryIntegrityEventRandomSampleGenerator();

        integrityAssessment.setIntegrityEvent(telemetryIntegrityEventBack);
        assertThat(integrityAssessment.getIntegrityEvent()).isEqualTo(telemetryIntegrityEventBack);

        integrityAssessment.integrityEvent(null);
        assertThat(integrityAssessment.getIntegrityEvent()).isNull();
    }

    @Test
    void modelRunTest() {
        IntegrityAssessment integrityAssessment = getIntegrityAssessmentRandomSampleGenerator();
        EnergyModelRun energyModelRunBack = getEnergyModelRunRandomSampleGenerator();

        integrityAssessment.setModelRun(energyModelRunBack);
        assertThat(integrityAssessment.getModelRun()).isEqualTo(energyModelRunBack);

        integrityAssessment.modelRun(null);
        assertThat(integrityAssessment.getModelRun()).isNull();
    }
}
