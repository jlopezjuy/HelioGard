package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.DroneMissionTestSamples.*;
import static ar.com.heliguard.app.domain.InstallationTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.RecoveryAssessmentTestSamples.*;
import static ar.com.heliguard.app.domain.VerificationResultTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VerificationResultTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(VerificationResult.class);
        VerificationResult verificationResult1 = getVerificationResultSample1();
        VerificationResult verificationResult2 = new VerificationResult();
        assertThat(verificationResult1).isNotEqualTo(verificationResult2);

        verificationResult2.setId(verificationResult1.getId());
        assertThat(verificationResult1).isEqualTo(verificationResult2);

        verificationResult2 = getVerificationResultSample2();
        assertThat(verificationResult1).isNotEqualTo(verificationResult2);
    }

    @Test
    void organizationTest() {
        VerificationResult verificationResult = getVerificationResultRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        verificationResult.setOrganization(organizationBack);
        assertThat(verificationResult.getOrganization()).isEqualTo(organizationBack);

        verificationResult.organization(null);
        assertThat(verificationResult.getOrganization()).isNull();
    }

    @Test
    void installationTest() {
        VerificationResult verificationResult = getVerificationResultRandomSampleGenerator();
        Installation installationBack = getInstallationRandomSampleGenerator();

        verificationResult.setInstallation(installationBack);
        assertThat(verificationResult.getInstallation()).isEqualTo(installationBack);

        verificationResult.installation(null);
        assertThat(verificationResult.getInstallation()).isNull();
    }

    @Test
    void droneMissionTest() {
        VerificationResult verificationResult = getVerificationResultRandomSampleGenerator();
        DroneMission droneMissionBack = getDroneMissionRandomSampleGenerator();

        verificationResult.setDroneMission(droneMissionBack);
        assertThat(verificationResult.getDroneMission()).isEqualTo(droneMissionBack);

        verificationResult.droneMission(null);
        assertThat(verificationResult.getDroneMission()).isNull();
    }

    @Test
    void recoveryAssessmentTest() {
        VerificationResult verificationResult = getVerificationResultRandomSampleGenerator();
        RecoveryAssessment recoveryAssessmentBack = getRecoveryAssessmentRandomSampleGenerator();

        verificationResult.setRecoveryAssessment(recoveryAssessmentBack);
        assertThat(verificationResult.getRecoveryAssessment()).isEqualTo(recoveryAssessmentBack);

        verificationResult.recoveryAssessment(null);
        assertThat(verificationResult.getRecoveryAssessment()).isNull();
    }
}
