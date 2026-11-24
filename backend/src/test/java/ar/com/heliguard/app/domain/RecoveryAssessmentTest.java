package ar.com.heliguard.app.domain;

import static ar.com.heliguard.app.domain.EnergyModelRunTestSamples.*;
import static ar.com.heliguard.app.domain.MaintenanceInterventionTestSamples.*;
import static ar.com.heliguard.app.domain.OrganizationTestSamples.*;
import static ar.com.heliguard.app.domain.RecoveryAssessmentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ar.com.heliguard.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RecoveryAssessmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecoveryAssessment.class);
        RecoveryAssessment recoveryAssessment1 = getRecoveryAssessmentSample1();
        RecoveryAssessment recoveryAssessment2 = new RecoveryAssessment();
        assertThat(recoveryAssessment1).isNotEqualTo(recoveryAssessment2);

        recoveryAssessment2.setId(recoveryAssessment1.getId());
        assertThat(recoveryAssessment1).isEqualTo(recoveryAssessment2);

        recoveryAssessment2 = getRecoveryAssessmentSample2();
        assertThat(recoveryAssessment1).isNotEqualTo(recoveryAssessment2);
    }

    @Test
    void organizationTest() {
        RecoveryAssessment recoveryAssessment = getRecoveryAssessmentRandomSampleGenerator();
        Organization organizationBack = getOrganizationRandomSampleGenerator();

        recoveryAssessment.setOrganization(organizationBack);
        assertThat(recoveryAssessment.getOrganization()).isEqualTo(organizationBack);

        recoveryAssessment.organization(null);
        assertThat(recoveryAssessment.getOrganization()).isNull();
    }

    @Test
    void interventionTest() {
        RecoveryAssessment recoveryAssessment = getRecoveryAssessmentRandomSampleGenerator();
        MaintenanceIntervention maintenanceInterventionBack = getMaintenanceInterventionRandomSampleGenerator();

        recoveryAssessment.setIntervention(maintenanceInterventionBack);
        assertThat(recoveryAssessment.getIntervention()).isEqualTo(maintenanceInterventionBack);

        recoveryAssessment.intervention(null);
        assertThat(recoveryAssessment.getIntervention()).isNull();
    }

    @Test
    void modelRunTest() {
        RecoveryAssessment recoveryAssessment = getRecoveryAssessmentRandomSampleGenerator();
        EnergyModelRun energyModelRunBack = getEnergyModelRunRandomSampleGenerator();

        recoveryAssessment.setModelRun(energyModelRunBack);
        assertThat(recoveryAssessment.getModelRun()).isEqualTo(energyModelRunBack);

        recoveryAssessment.modelRun(null);
        assertThat(recoveryAssessment.getModelRun()).isNull();
    }
}
